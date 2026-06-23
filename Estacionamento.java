import java.io.*;
public class Estacionamento {

	// Vetores de marcas
	static String codMarcaVeic[] = {"BM", "VW", "FO", "MB", "CV", "FI", "AU", "TO", "HO", "HY"};
	static String descricaoMarca[] = {"BMW", "Volkswagen", "Ford", "Mercedes Benz", "Chevrolet", "Fiat", "Audi", "Toyota", "Honda", "Hyundai"};

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
		// metodo para alterar o valor do campo ATIVO para N
		try {
			RandomAccessFile arqEstacionamento = new RandomAccessFile("EST.DAT", "rw");			
			arqEstacionamento.seek(posicao);
			arqEstacionamento.writeChar('N');   // desativar o registro antigo
			arqEstacionamento.close();
		}catch (IOException e) { 
			System.out.println("Erro na abertura do arquivo  -  programa sera finalizado");
			System.exit(0);
		}
	}
	
	public static String consistirCategoria (String categoriaVeiculo) {
		switch (categoriaVeiculo.toUpperCase()) {
		case "GI" :
			return "Grande e Importado";
		case "PI" :
			return "Pequeno e Importado";
		case "GN" :
			return "Grande e Nacional";
		case "PN" :
			return "Pequeno e Nacional";
		default:
			return "ERRO";
		}
	}

	// ***********************   REGISTRAR ENTRADA DO VEÍCULO   *****************************
	public void entrarVeiculo() {
		String codEstChave = "000001";
		int maiorCodEst = 0;
		Main.leia.nextLine();
		while (true) {
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
			placa = Main.leia.next();
			
			if (placa.equalsIgnoreCase("FIM")) {
				break;
			}
			
			if (placa.length() != 7) {
				System.out.println("Placa Inválida!");
				break;
			}
			
			boolean placaValida = true;
			
			for (int i = 0; i < 3; i++) {
				char c = placa.charAt(i);
				if (c < 'A' || c > 'Z') {
					placaValida = false;
					break;
				}
			}
			
			for (int i = 3; i <7; i++) {
				char c = placa.charAt(i);
				if (c < '0' || c > '9') {
					placaValida = false;
					break;
				}
			}
			
			if (!placaValida) {
				System.out.println("Placa Inválida. Formato ABC1234!");
				break;
			}
			
			// Validar veículo já estacionado
			boolean jaEstacionado = false;
			try {
				RandomAccessFile arqEstacionamento = new RandomAccessFile("EST.DAT", "rw");
				while (true) {
					ativo            = arqEstacionamento.readChar();
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
					
					if (ativo == 'S' && tipoOperacao == 'E') {
						jaEstacionado = true;
						break;
					}
				}
				arqEstacionamento.close();
			} catch (EOFException e) {
				// Fim do arquivo
			} catch (IOException e) {
				// Arquivo não existe
			}
			
			if (jaEstacionado) {
				System.out.println("Erro: Esse veículo já está estacionado");
				break;
			}
			
			// Validar dataOperacao
			while (true) {
				System.out.print("Digite a data da entrada (DD/MM/AAAA): ");
				dataOperacao = Main.leia.nextLine();
				if (!dataEhValida(dataOperacao)) {
					System.out.println("Data Inválida!");
				} else {
					break;
				}
			}
			
			// Validar ModeloCor
			while (true) {
				System.out.print("Digite o modelo e a cor do veículo (mínimo 10 caracteres): ");
				modeloCor = Main.leia.nextLine();
				if (modeloCor.length() < 10) {
					System.out.println("Erro! Pelo menos 10 caracteres!");
				} else {
					break;
				}
			}
			
			// Validar Marca
			int indiceMarcaVeic = -1;
			do {
				System.out.print("Digite a sigla da marca do veículo (ex: VW, FI): ");
				marca = Main.leia.nextLine().toUpperCase();
				indiceMarcaVeic = pesquisarMarcaVeiculo(marca);
				if (indiceMarcaVeic == -1) {
					System.out.println("Não encontrou a marca do veículo!");
				} else {
					System.out.println("A marca do veículo é " + descricaoMarca[indiceMarcaVeic]);
				}
			} while (indiceMarcaVeic == -1);

			// Validar Categoria
			String descCategoria = "ERRO";
			do {
				System.out.print("Digite a categoria do veículo (GI, PI, GN, PN): ");
				categoriaVeiculo = Main.leia.nextLine().toUpperCase();
				descCategoria = consistirCategoria(categoriaVeiculo);
				if (descCategoria.equals("ERRO")) {
					System.out.println("Categoria Inválida!");
				} else {
					System.out.println("Descrição: " + descCategoria);
				}
			} while (descCategoria.equals("ERRO"));

			// Validar Hora
			while (true) {
				System.out.print("Digite a hora de entrada (HH:MM): ");
				horaEntrada = Main.leia.nextLine();
				if (!horaEhValida(horaEntrada)) {
					System.out.println("Hora Inválida!");
				} else {
					break;
				}
			}

			tipoOperacao = 'E';
			horaSaida = "";
			valorPago = 0;

			salvarRegistroVeiculo();
		}
	}

	// ****************** Pesquisar Marca do Veículo ******************
	public int pesquisarMarcaVeiculo(String codMarcaPesq) {
		for (byte x = 0; x < codMarcaVeic.length; x++) {
			if (codMarcaPesq.equals(codMarcaVeic[x])) {
				return x;
			}
		}
		return -1;
	}
	
	// **************** Calcular Valor a pagar ********************
	public float calcularValorAPagar(String horaEntrada, String horaSaida, String categoriaVeiculo) {
		int he = Integer.parseInt(horaEntrada.substring(0, 2));
		int me = Integer.parseInt(horaEntrada.substring(3, 5));
		int hs = Integer.parseInt(horaSaida.substring(0, 2));
		int ms = Integer.parseInt(horaSaida.substring(3, 5));
		
		float vlrHora = 0;
		boolean ate18 = (he < 18) || (he == 18 && me == 0);
		String cat = categoriaVeiculo.toUpperCase();
		
		if  (ate18) {
			if (cat.equals("GI")) {
				vlrHora = 10;
			} else if (cat.equals("PI")) {
				vlrHora = (float) 8.2;
			} else if (cat.equals("GN")) {
				vlrHora = 9;
			} else if (cat.equals("PN")) {
				vlrHora = 7;
			}
		} else {
			if (cat.equals("GI")) {
				vlrHora = 8;
			} else if (cat.equals("PI")) {
				vlrHora = (float) 6.5;
			} else if (cat.equals("GN")) {
				vlrHora = (float) 7.5;
			} else if (cat.equals("PN")) {
				vlrHora = 6;
			}
		}
		
		return (hs - he + (ms - me) / 60.0f) * vlrHora;
	}

	// ***********************   REGISTRAR SAÍDA DO VEÍCULO   *****************************
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
				
			// Confirmação da saída e pagamento
			// Pedir hora de saída
			String horaSaidaDigitada;
			while (true) {
				System.out.print("Digite a hora de saída (HH:MM): ");
				horaSaidaDigitada = Main.leia.nextLine();
				if (!horaEhValida(horaSaidaDigitada)) {
					System.out.println("Hora Inválida!");
				} else if (!horaSaidaMaiorQueEntrada(horaEntrada, horaSaidaDigitada)) {
					System.out.println("Hora de saída deve ser maior que a hora de entrada (" + horaEntrada + ")!");
				} else {
					break;
				}
			}
			
			float valorPagoCalculado = calcularValorAPagar(horaEntrada, horaSaidaDigitada, categoriaVeiculo);
			System.out.println("Valor a pagar: R$ " + String.format("%.2f", valorPagoCalculado));
				
			// Confirmação da saída e pagamento
			char confirmacao = ' ';
			do {
				System.out.print("Confirma a saída do veículo? (S/N): ");
				String input = Main.leia.nextLine().toUpperCase();
				if (input.length() > 0) {
					confirmacao = input.charAt(0);
				}
			} while (confirmacao != 'S' && confirmacao != 'N');
				
			if (confirmacao == 'S') {
				desativarRegistroVeiculo(posicaoRegistro);
					
				ativo = 'S';
				tipoOperacao = 'S';
				horaSaida = horaSaidaDigitada;
				valorPago = valorPagoCalculado;
					
				salvarRegistroVeiculo();
				System.out.println("Saída registrada com sucesso!");
			} else {
				System.out.println("Saída do veículo cancelada.");
			}
			break;
		} while (!codEstChave.equalsIgnoreCase("FIM"));
	}
	
	// Método para validar hora
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

	// ***********************  CONSULTA  *****************************
	public void consultarVeiculo() {
		RandomAccessFile arqEst;
		byte opcao;
		String codEstChave;
		String dataFiltro;
		long posicaoRegistro;

		do {
			do {
				System.out.println("\n ***************  CONSULTA  ***************** ");
				System.out.println(" [1] EXIBIR TODOS OS REGISTROS ");
				System.out.println(" [2] VEICULOS QUE AINDA NAO SAÍRAM ");
				System.out.println(" [3] REGISTROS POR DATA ");
				System.out.println(" [0] SAIR ");
				System.out.print("\nDigite a opcao desejada: ");
				opcao = Main.leia.nextByte();
				if (opcao < 0 || opcao > 3) {
					System.out.println("Opcao invalida, digite novamente.\n");
				}
			} while (opcao < 0 || opcao > 3);

			switch (opcao) {
			case 0:
				System.out.println("\n ************  SAINDO DA CONSULTA  ************** \n");
				break;

			case 1:  // exibir todos os registros ativos
				try {
					arqEst = new RandomAccessFile("EST.DAT", "rw");
					imprimirCabecalhoConsulta();
					while (true) {
						ativo            = arqEst.readChar();
						codEst           = arqEst.readUTF();
						placa            = arqEst.readUTF();
						dataOperacao     = arqEst.readUTF();
						tipoOperacao     = arqEst.readChar();
						modeloCor        = arqEst.readUTF();
						marca         = arqEst.readUTF();
						categoriaVeiculo = arqEst.readUTF();
						horaEntrada      = arqEst.readUTF();
						horaSaida        = arqEst.readUTF();
						valorPago        = arqEst.readFloat();
						if (ativo == 'S') {
							imprimirLinhaConsulta();
						}
					}
				} catch (EOFException e) {
					System.out.println("\n FIM DE RELATORIO - ENTER para continuar...\n");
					Main.leia.nextLine();
					Main.leia.nextLine();
				} catch (IOException e) {
					System.out.println("Erro na abertura do arquivo - programa sera finalizado");
					System.exit(0);
				}
				break;

			case 2:  // veiculos que ainda nao saíram (tipo E sem saida)
				try {
					arqEst = new RandomAccessFile("EST.DAT", "rw");
					imprimirCabecalhoConsulta();
					while (true) {
						ativo            = arqEst.readChar();
						codEst           = arqEst.readUTF();
						placa            = arqEst.readUTF();
						dataOperacao     = arqEst.readUTF();
						tipoOperacao     = arqEst.readChar();
						modeloCor        = arqEst.readUTF();
						marca         = arqEst.readUTF();
						categoriaVeiculo = arqEst.readUTF();
						horaEntrada      = arqEst.readUTF();
						horaSaida        = arqEst.readUTF();
						valorPago        = arqEst.readFloat();
						if (ativo == 'S' && tipoOperacao == 'E') {
							imprimirLinhaConsulta();
						}
					}
				} catch (EOFException e) {
					System.out.println("\n FIM DE RELATORIO - ENTER para continuar...\n");
					Main.leia.nextLine();
					Main.leia.nextLine();
				} catch (IOException e) {
					System.out.println("Erro na abertura do arquivo - programa sera finalizado");
					System.exit(0);
				}
				break;

			case 3:  // registros de uma data especifica
				Main.leia.nextLine();
				do {
					System.out.print("Digite a Data para filtro (DD/MM/AAAA): ");
					dataFiltro = Main.leia.nextLine();
					if (!dataEhValida(dataFiltro)) {
						System.out.println("! Data invalida. Use o formato DD/MM/AAAA.");
					}
				} while (!dataEhValida(dataFiltro));

				try {
					arqEst = new RandomAccessFile("EST.DAT", "rw");
					imprimirCabecalhoConsulta();
					while (true) {
						ativo            = arqEst.readChar();
						codEst           = arqEst.readUTF();
						placa            = arqEst.readUTF();
						dataOperacao     = arqEst.readUTF();
						tipoOperacao     = arqEst.readChar();
						modeloCor        = arqEst.readUTF();
						marca         = arqEst.readUTF();
						categoriaVeiculo = arqEst.readUTF();
						horaEntrada      = arqEst.readUTF();
						horaSaida        = arqEst.readUTF();
						valorPago        = arqEst.readFloat();
						if (ativo == 'S' && dataOperacao.equals(dataFiltro)) {
							imprimirLinhaConsulta();
						}
					}
				} catch (EOFException e) {
					System.out.println("\n FIM DE RELATORIO - ENTER para continuar...\n");
					Main.leia.nextLine();
				} catch (IOException e) {
					System.out.println("Erro na abertura do arquivo - programa sera finalizado");
					System.exit(0);
				}
				break;
			}

		} while (opcao != 0);
	}

	// ***********************  METODOS DE IMPRESSAO  *****************************
	public void imprimirCabecalhoConsulta() {
		System.out.println("\nPlaca      OP    Modelo e Cor           Marca        Categ  Data        Hr Entr  Hr Saida  Vlr Pago");
		System.out.println("---------- ---   ---------------------- ------------ ------ ----------- -------- --------- --------");
	}

	public void imprimirLinhaConsulta() {
		int idx = pesquisarMarcaVeiculo(marca);
		String descMarca = (idx != -1) ? descricaoMarca[idx] : marca;
		String vlrStr = (tipoOperacao == 'S') ? String.format("%.2f", valorPago) : "";
		System.out.println(
				formatarString(placa, 10) + " " +
						formatarString(String.valueOf(tipoOperacao), 5) + " " +
						formatarString(modeloCor, 22) + " " +
						formatarString(descMarca, 12) + " " +
						formatarString(categoriaVeiculo, 6) + " " +
						formatarString(dataOperacao, 11) + " " +
						formatarString(horaEntrada, 8) + " " +
						formatarString(horaSaida, 9) + " " +
						vlrStr
				);
	}

	public void imprimirCabecalhoRelatorio() {
		System.out.println("\nPLACA      MODELO E COR             DATA        HR ENTR  HR SAIDA  VLR. PAGO");
		System.out.println("---------- ----------------------- ----------- -------- --------- ----------");
	}

	public void imprimirLinhaRelatorio() {
		System.out.println(
				formatarString(placa, 10) + " " +
						formatarString(modeloCor, 23) + " " +
						formatarString(dataOperacao, 11) + " " +
						formatarString(horaEntrada, 8) + " " +
						formatarString(horaSaida, 9) + " " +
						String.format("%,.2f", valorPago)
				);
	}

	public static String formatarString(String texto, int tamanho) {
		if (texto == null) { texto = ""; }
		if (texto.length() > tamanho) {
			texto = texto.substring(0, tamanho);
		} else {
			while (texto.length() < tamanho) {
				texto = texto + " ";
			}
		}
		return texto;
	}
	
	// ***********************  EXCLUSAO  *****************************
	public void excluirVeiculo() {
		String codEstChave;
		char confirmacao;
		long posicaoRegistro = 0;

		do {
			do {
				Main.leia.nextLine();
				System.out.println("\n ***************  EXCLUSAO  ***************** ");
				System.out.print("Digite o Codigo (codEst) para excluir ( FIM para encerrar ): ");
				codEstChave = Main.leia.nextLine();
				if (codEstChave.equals("FIM")) {
					break;
				}
				try {
					int numCod = Integer.parseInt(codEstChave);
					String codFormatado = String.valueOf(numCod);
					while (codFormatado.length() < 6) {
						codFormatado = "0" + codFormatado;
					}
					codEstChave = codFormatado;
				} catch (NumberFormatException e) {
					System.out.println("! Codigo invalido.");
					codEstChave = "";
					posicaoRegistro = -1;
					continue;
				}

				posicaoRegistro = pesquisarRegistroVeiculo(codEstChave);
				if (posicaoRegistro == -1) {
					System.out.println("! Codigo nao encontrado no arquivo, tente novamente.\n");
				}
			} while (posicaoRegistro == -1);

			if (codEstChave.equals("FIM")) {
				System.out.println("\n ************  PROGRAMA ENCERRADO  ************** \n");
				break;
			}

			System.out.println("Codigo  : " + codEst);
			System.out.println("Placa   : " + placa);
			System.out.println("Modelo  : " + modeloCor);
			System.out.println("Data    : " + dataOperacao);
			System.out.println("Tipo    : " + tipoOperacao);
			System.out.println();

			do {
				System.out.print("\nConfirma a exclusao deste registro (S/N) ? ");
				confirmacao = Main.leia.next().charAt(0);
				confirmacao = Character.toUpperCase(confirmacao);
				if (confirmacao == 'S') {
					desativarRegistroVeiculo(posicaoRegistro);
					System.out.println("Registro excluido com sucesso !\n");
				}
			} while (confirmacao != 'S' && confirmacao != 'N');

		} while (!codEst.equals("FIM"));
	}


	// ***********************  RELATORIO DE FATURAMENTO  *****************************
	public void emitirFaturamento() {
		RandomAccessFile arqEst;
		float totalFaturado = 0;
		String placaFiltro;
		int count = 0;

		Main.leia.nextLine();
		System.out.println("\n ***************  RELATORIO DE FATURAMENTO  ***************** ");
		System.out.print("Digite a PLACA para filtrar (ENTER para todos): ");
		placaFiltro = Main.leia.nextLine().toUpperCase();

		try {
			arqEst = new RandomAccessFile("EST.DAT", "rw");
			imprimirCabecalhoRelatorio();
			while (true) {
				ativo            = arqEst.readChar();
				codEst           = arqEst.readUTF();
				placa            = arqEst.readUTF();
				dataOperacao     = arqEst.readUTF();
				tipoOperacao     = arqEst.readChar();
				modeloCor        = arqEst.readUTF();
				marca         = arqEst.readUTF();
				categoriaVeiculo = arqEst.readUTF();
				horaEntrada      = arqEst.readUTF();
				horaSaida        = arqEst.readUTF();
				valorPago        = arqEst.readFloat();

				if (ativo == 'S' && tipoOperacao == 'S') {
					if (placaFiltro.equals("") || placaFiltro.equals(placa)) {
						imprimirLinhaRelatorio();
						totalFaturado = totalFaturado + valorPago;
						count = count + 1;
					}
				}
			}
		} catch (EOFException e) {
			System.out.println("--------------------------------------------------------------------");
			if (count == 0) {
				System.out.println("Nenhum registro encontrado para os criterios informados.");
			} else {
				System.out.printf("TOTAL FATURADO: R$: " + totalFaturado);
			}
			System.out.println("\n FIM DE RELATORIO - ENTER para continuar...\n");
			Main.leia.nextLine();
		} catch (IOException e) {
			System.out.println("Erro na abertura do arquivo - programa sera finalizado");
			System.exit(0);
		}
	}
	
	// ***********************  CONSISTENCIAS  *****************************
	public boolean dataEhValida(String data) {
		int dia, mes, ano;
		
		if (data.length() != 10 || data.charAt(2) != '/' || data.charAt(5) != '/') {
			System.out.println("Data inválida, digite 10 caracteres no formato DD/MM/AAAA");
			return false;
		}
		
		try {
			dia = Integer.parseInt( data.substring(0,2) );
			mes = Integer.parseInt( data.substring(3,5) );
			ano = Integer.parseInt( data.substring(6) );
		} catch (NumberFormatException erro) {
			System.out.println("Data inválida, digite dia, mes e ano numéricos");
			return false;
		}
		
		if (ano > 2026) {
			System.out.println("Data Inválida, digite ano máximo 2026");
			return false;
		}
		
		if (mes < 1 || mes > 12) {
			System.out.println("Data Inválida, digite mes entre 1 e 12");
			return false;
		}
		
		if (dia < 1 || dia > 31) {
			System.out.println("Data inválida, digite dia entre 1 e 31");
			return false;
		}
		
		if ((mes == 4 || mes == 6 || mes == 9 || mes == 11) && dia > 30) {
			System.out.println("Data inválida, meses 4, 6, 9 e 11, digite dia até 30");
			return false;
		}
		
		if (mes == 2 ) {
			if (ano % 4 == 0 && ano % 100 != 0 || ano % 400 == 0) { // ano bissexto
				if (dia > 29) {
					System.out.println("Data Inválida, Fevereiro em ano bissexto máximo 29 dias");
					return false;
				}
			} else {
				if (dia > 28) {
					System.out.println("Data Inválida, Fevereiro neste ano, máximo 28 dias");
					return false;
				}
			}
		}
		
		return true;
	}

	public boolean horaEhValida(String hora) {
		if (hora.length() != 5) { return false; }
		if (hora.charAt(2) != ':') { return false; }
		try {
			int h = Integer.parseInt(hora.substring(0, 2));
			int m = Integer.parseInt(hora.substring(3, 5));
			return h >= 0 && h <= 23 && m >= 0 && m <= 59;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public boolean horaSaidaMaiorQueEntrada(String horaEnt, String horaSai) {
		int he = Integer.parseInt(horaEnt.substring(0, 2)) * 60 + Integer.parseInt(horaEnt.substring(3, 5));
		int hs = Integer.parseInt(horaSai.substring(0, 2)) * 60 + Integer.parseInt(horaSai.substring(3, 5));
		return hs > he;
	}
}
