import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Estacionamento {

	// Arrays estáticos de marcas desenvolvidos pela Sarah (com o erro lógico de ser uma única string)
	static String codMarcaVeic[] = {"BM, VW, FO, MB, CV, FI, AU, TO, HO, HY"};
	static String descricaoMarca[] = {"BMW, Volkswagen, Ford, Mercedes Benz, Chevrolet, Fiat, Audi, Toyota, honda, hyundai"};

	char 	ativo;
	String	codEst; // Chave Primária, que indica qual é a entrada do veículo. Um mesmo veículo pode ter vários codEst.
	String 	placa; // XXX9999
	String 	dataOperacao; // DD/MM/AAAA
	char    tipoOperacao;  // E ou S, entrada ou saída
	String  modeloCor; 
	String  marca; 
	String  categoriaVeiculo; 
	String  horaEntrada; 
	String  horaSaida; 
	float   valorPago; 
	
	public long pesquisarRegistroVeiculo(String codEstPesq) {	
		// metodo para localizar um registro no arquivo em disco
		long posicaoCursorArquivo = 0;
		try { 
			RandomAccessFile arqEstacionamento = new RandomAccessFile("EST.DAT", "rw");
			while (true) {
				posicaoCursorArquivo  = arqEstacionamento.getFilePointer();	// posicao do inicio do registro no arquivo
				ativo		     = arqEstacionamento.readChar();
				codEst           = arqEstacionamento.readUTF();
				placa            = arqEstacionamento.readUTF();
				dataOperacao     = arqEstacionamento.readUTF();
				tipoOperacao     = arqEstacionamento.readChar();
				modeloCor        = arqEstacionamento.readUTF();
				marca            = arqEstacionamento.readUTF();
				categoriaVeiculo = arqEstacionamento.readUTF();
				horaEntrada      = arqEstacionamento.readUTF();
				horaSaida        = arqEstacionamento.readUTF();
				valorPago        = arqEstacionamento.readFloat();      

				if ( codEstPesq.equals(codEst) && ativo == 'S') {
					arqEstacionamento.close();
					return posicaoCursorArquivo;
				}
			}
		}catch (EOFException e) {
			return -1; // registro nao foi encontrado
		}catch (IOException e) { 
			System.out.println("Erro na abertura do arquivo  -  programa sera finalizado");
			System.exit(0);
			return -1;
		}
	}

	public void salvarRegistroVeiculo() {	
		// metodo para incluir um novo registro no final do arquivo em disco
		try {
			RandomAccessFile arqEstacionamento = new RandomAccessFile("EST.DAT", "rw");	
			arqEstacionamento.seek(arqEstacionamento.length());  // posiciona o ponteiro no final do arquivo (EOF)
			arqEstacionamento.writeChar(ativo);
			arqEstacionamento.writeUTF(codEst);
			arqEstacionamento.writeUTF(placa);
			arqEstacionamento.writeUTF(dataOperacao);
			arqEstacionamento.writeChar(tipoOperacao);
			arqEstacionamento.writeUTF(modeloCor);
			arqEstacionamento.writeUTF(marca);
			arqEstacionamento.writeUTF(categoriaVeiculo);
			arqEstacionamento.writeUTF(horaEntrada);
			arqEstacionamento.writeUTF(horaSaida);
			arqEstacionamento.writeFloat(valorPago);
			arqEstacionamento.close();
			System.out.println("Dados gravados com sucesso !\n");
		}catch (IOException e) { 
			System.out.println("Erro na abertura do arquivo  -  programa sera finalizado");
			System.exit(0);
		}
	}

	public void desativarRegistroVeiculo(long posicao)	{    
		// metodo para alterar o valor do campo ATIVO para N (Thiago/Sarah - com o erro de usar ALUNO.DAT)
		try {
			RandomAccessFile arqEstacionamento = new RandomAccessFile("ALUNO.DAT", "rw");			
			arqEstacionamento.seek(posicao);
			arqEstacionamento.writeChar('N');   // desativar o registro antigo
			arqEstacionamento.close();
		}catch (IOException e) { 
			System.out.println("Erro na abertura do arquivo  -  programa sera finalizado");
			System.exit(0);
		}
	}

	// ***********************   REGISTRAR ENTRADA DO VEÍCULO   *****************************
	public void entrarVeiculo() {
		String codEstChave = "000001";
		int maiorCodEst = 0;
		
		try {
			RandomAccessFile arqEstacionamento = new RandomAccessFile("EST.DAT", "rw");
			while (true) {
				ativo		     = arqEstacionamento.readChar();
				codEst           = arqEstacionamento.readUTF();
				placa            = arqEstacionamento.readUTF();
				dataOperacao     = arqEstacionamento.readUTF();
				tipoOperacao     = arqEstacionamento.readChar();
				modeloCor        = arqEstacionamento.readUTF();
				marca            = arqEstacionamento.readUTF();
				categoriaVeiculo = arqEstacionamento.readUTF();
				horaEntrada      = arqEstacionamento.readUTF();
				horaSaida        = arqEstacionamento.readUTF();
				valorPago        = arqEstacionamento.readFloat();
				
				if (Integer.parseInt(codEst) > maiorCodEst && ativo == 'S') {
					maiorCodEst = Integer.parseInt(codEst);
				}
			}
		} catch (EOFException e) {
			// gerar o próximo codEst
			if (maiorCodEst > 0) {
				maiorCodEst ++;
				codEstChave =  String.valueOf(maiorCodEst);
				while (codEstChave.length() < 6) {
					codEstChave = '0' + codEstChave;
				}
			}
		} catch (IOException e) {
			System.out.println("Erro na abertura do arquivo - Programa será finalizado");
			System.exit(0);
		}
		
		ativo = 'S';
		codEst = codEstChave;
		System.out.println("Digite a placa do veículo no formato XXX0000 (FIM para encerrar): ");
		placa = Main.leia.next(); // Leitura da placa
		if (placa.equalsIgnoreCase("FIM")) {
			return;
		}
		
		// Limpa buffer e chama a validação desenvolvida por Cachaça e João
		Main.leia.nextLine();
		validar();
	}

	// Método de validação de dados de entrada desenvolvido por Cachaça e João
	public void validar() {
		boolean valida = false;
		do {
			System.out.println("Digite a data de entrada do veículo DD/MM/AAAA: ");
			dataOperacao = Main.leia.nextLine();
			try {
				DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				LocalDate.parse(dataOperacao, formato);
				valida = true;
			} catch (DateTimeParseException e) {
				System.out.println("Data Inválida!");
			}
		} while (!valida);

		tipoOperacao = 'E';

		System.out.println("Digite o Modelo/Cor do veículo: ");
		modeloCor = Main.leia.nextLine();

		// Lógica de pesquisa da marca desenvolvida pela Sarah
		int indiceMarcaVeic = -1;
		do {
			System.out.println("Digite a marca do veículo: ");
			marca = Main.leia.nextLine();
			indiceMarcaVeic = pesquisarMarcaVeic(marca);
			if (indiceMarcaVeic == -1) {
				System.out.println("Não encontrou a marca do veículo!");
			} else {
				System.out.println("A marca do veículo é " + descricaoMarca[indiceMarcaVeic]);
				marca = descricaoMarca[indiceMarcaVeic];
			}
		} while (indiceMarcaVeic == -1);

		System.out.println("Digite a categoria do veículo: ");
		categoriaVeiculo = Main.leia.nextLine();

		System.out.println("Digite a hora de entrada do veículo: ");
		horaEntrada = Main.leia.nextLine();

		horaSaida = "";
		valorPago = 0;

		// Observação: os alunos não chamaram salvarRegistroVeiculo() aqui, portanto a entrada não é gravada em disco.
	}

	public static int pesquisarMarcaVeic(String codMarca) {
		for (byte x = 0; x < codMarcaVeic.length; x++) {
			if (codMarca.equals(codMarcaVeic[x])) {
				return x;
			}
		}
		return -1;
	}

	// ***********************   REGISTRAR SAÍDA DO VEÍCULO (Thiago)   *****************************
	public void sairVeiculo() {
		String codEstChave;
		long posicaoRegistro;
		
		// Buffer
		Main.leia.nextLine();
		
		do {
			System.out.println("Digite o código de estacionamento do veículo que está pra sair (FIM para cancelar): ");
			codEstChave = Main.leia.nextLine();
			if (codEstChave.equalsIgnoreCase("FIM")) {
				break;
			}
			
			posicaoRegistro = pesquisarRegistroVeiculo(codEstChave);
			if (posicaoRegistro == -1) {
				System.out.println("O código do veículo digitado não existe no arquivo");
				break;
			}
			
			// Registro carregado e valores na memória
			if (tipoOperacao == 'S') {
				System.out.println("Esse veículo já saiu do estacionamento");
				break;
			}
			
			// Exibir dados do veículo
			System.out.println("--- DADOS ---");
			System.out.println("Código de Estacionamento: " + codEst);
			System.out.println("Placa do Veículo: " + placa);
			System.out.println("Modelo e cor: " + modeloCor);
			System.out.println("Marca do Veículo: " + marca);
			System.out.println("Categoria: " + categoriaVeiculo);
			System.out.println("Data de Entrada: " + dataOperacao);
			System.out.println("Hora da Entrada: " + horaEntrada);
			System.out.println("-------------------");
			
			// Solicitar e validar hora de saída
			String horaSaidaDigitada;
			while (true) {
				System.out.println("Digite a hora de saída do veículo (HH:MM): ");
				horaSaidaDigitada = Main.leia.nextLine();
				if (!validarHora(horaSaidaDigitada)) {
					System.out.println("Formato de hora inválido! Digite no formato HH:MM (00:00 até 23:59)");
					break;
				}
				
				// Comparar horaSaida com HoraEntrada
				int he = Integer.parseInt(horaEntrada.substring(0, 2));
				int me = Integer.parseInt(horaEntrada.substring(3, 5));
				int hs = Integer.parseInt(horaSaidaDigitada.substring(0, 2));
				int ms = Integer.parseInt(horaSaidaDigitada.substring(3, 5));
				
				int totalMinutosEntrada = he * 60 + me;
				int totalMinutosSaida = hs * 60 + ms;
				
				// Calcular valor a ser pago (com o erro lógico de aninhamento do Thiago)
				float vlrHora = 0;
				boolean ate18 = (he < 18) || (he == 18 && me == 0);
				String cat = categoriaVeiculo.toUpperCase();
				
				if (ate18) {
					if (cat.equals("GI")) {
						vlrHora = 10;
					} else if (cat.equals("PI")) {
						vlrHora = (float)8.20;
					} else if (cat.equals("GN")) {
						vlrHora = 9;
					} else if (cat.equals("PN")) {
						vlrHora = 7;
					} else {
						if (cat.equals("GI")) {
							vlrHora = 8;
						} else if (cat.equals("PI")) {
							vlrHora = (float)6.5;
						} else if (cat.equals("GN")) {
							vlrHora = 9;
						} else if (cat.equals("PN")) {
							vlrHora = 6;
						}
					}
				}
				
				float valorPagoCalculado = (hs - he + (ms - me) / (float)60) * vlrHora;
				System.out.println("Valor pago: R$" + valorPagoCalculado);
				
				// Confirmação da saída e pagamento
				char confirmacao;
				do {
					System.out.println("Confirma a saída do veículo? (S/N): ");
					confirmacao = Main.leia.next().charAt(0);
				} while (confirmacao != 'S' && confirmacao != 'N');
				
				if (confirmacao == 'S') {
					desativarRegistroVeiculo(posicaoRegistro);
					
					ativo = 'S';
					tipoOperacao = 'S';
					horaSaida = horaSaidaDigitada;
					valorPago = valorPagoCalculado;
					
					salvarRegistroVeiculo();
					System.out.println("Saída registrada com sucesso");
				} else {
					System.out.println("Saída do veículo cancelada");
				}
				break;
			}
			break;
		} while (!codEstChave.equalsIgnoreCase("FIM"));
	}
	
	// Método para validar hora desenvolvido pelo Thiago
	public boolean validarHora(String hora) {
		if (hora == null || hora.length() != 5) {
			return false;
		}
		if (hora.charAt(2) != ':') {
			return false;
		}
		for (int i = 0; i < 5; i++) {
			if (i == 2) {
				continue;
			}
			if (!Character.isDigit(hora.charAt(i))) {
				return false;
			}
		}
		int h = Integer.parseInt(hora.substring(0, 2));
		int m = Integer.parseInt(hora.substring(3, 5));
		return h >= 0 && h <= 23 && m >= 0 && m <= 59;
	}
}
