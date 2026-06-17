import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Estacionamento {

	char ativo;               // 'S' = Ativo, 'N' = Inativo/Excluído
	String codEst;            // Chave Primária de controle (6 dígitos, ex: "000001")
	String placa;             // Placa do veículo (formato XXX0000)
	String dataOperacao;      // Data da operação (DD/MM/AAAA)
	char tipoOperacao;        // 'E' = Entrada, 'S' = Saída
	String modeloCor;         // Modelo e Cor do veículo
	String marca;             // Nome por extenso da marca do veículo
	String categoriaVeiculo;  // Categoria ("GI", "PI", "GN", "PN")
	String horaEntrada;       // Hora de entrada (HH:MM)
	String horaSaida;         // Hora de saída (HH:MM)
	float valorPago;          // Valor pago na saída

	// Tabelas de validação de marcas (Sarah)
	static final String[] COD_MARCA_VEIC = {"BM", "VW", "FO", "MB", "CV", "FI", "AU", "TO", "HO", "HY"};
	static final String[] DESCRICAO_MARCA = {
		"BMW", "Volkswagen", "Ford", "Mercedes Benz", "Chevrolet", 
		"Fiat", "Audi", "Toyota", "Honda", "Hyundai"
	};

	// Pesquisa de registros por código de estacionamento
	public long pesquisarRegistroVeiculo(String codEstPesq) {
		long posicaoCursorArquivo = 0;
		try {
			RandomAccessFile arqEstacionamento = new RandomAccessFile("EST.DAT", "r");
			while (true) {
				posicaoCursorArquivo = arqEstacionamento.getFilePointer();
				ativo = arqEstacionamento.readChar();
				codEst = arqEstacionamento.readUTF();
				placa = arqEstacionamento.readUTF();
				dataOperacao = arqEstacionamento.readUTF();
				tipoOperacao = arqEstacionamento.readChar();
				modeloCor = arqEstacionamento.readUTF();
				marca = arqEstacionamento.readUTF();
				categoriaVeiculo = arqEstacionamento.readUTF();
				horaEntrada = arqEstacionamento.readUTF();
				horaSaida = arqEstacionamento.readUTF();
				valorPago = arqEstacionamento.readFloat();

				if (codEstPesq.equals(codEst) && ativo == 'S') {
					arqEstacionamento.close();
					return posicaoCursorArquivo;
				}
			}
		} catch (EOFException e) {
			return -1;
		} catch (FileNotFoundException e) {
			return -1;
		} catch (IOException e) {
			System.out.println("Erro na leitura do arquivo EST.DAT - " + e.getMessage());
			return -1;
		}
	}

	// Salva o registro atual no final do arquivo
	public void salvarRegistroVeiculo() {
		try {
			RandomAccessFile arqEstacionamento = new RandomAccessFile("EST.DAT", "rw");
			arqEstacionamento.seek(arqEstacionamento.length()); // Posiciona no final do arquivo

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
		} catch (IOException e) {
			System.out.println("Erro ao gravar dados no arquivo - " + e.getMessage());
			System.exit(0);
		}
	}

	// Desativa o registro na posição especificada (exclusão lógica)
	public void desativarRegistroVeiculo(long posicao) {
		try {
			RandomAccessFile arqEstacionamento = new RandomAccessFile("EST.DAT", "rw");
			arqEstacionamento.seek(posicao);
			arqEstacionamento.writeChar('N'); // Altera o campo ativo para 'N'
			arqEstacionamento.close();
		} catch (IOException e) {
			System.out.println("Erro ao desativar o registro - " + e.getMessage());
			System.exit(0);
		}
	}

	// Pesquisa índice da marca
	public static int pesquisarMarcaVeic(String codMarca) {
		for (int i = 0; i < COD_MARCA_VEIC.length; i++) {
			if (codMarca.equalsIgnoreCase(COD_MARCA_VEIC[i])) {
				return i;
			}
		}
		return -1;
	}

	// Validador de formato de hora (Thiago)
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

	// Validador de formato de data (Cachaça e João)
	public boolean validarData(String data) {
		try {
			DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDate.parse(data, formato);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	// *********************** 1. REGISTRAR ENTRADA DE VEÍCULO ***********************
	public void entrarVeiculo() {
		System.out.println("\n--- REGISTRAR ENTRADA DO VEÍCULO ---");
		Main.leia.nextLine(); // Limpar buffer

		// Gerar próximo codEst
		int maiorCodEst = 0;
		try {
			RandomAccessFile arqEstacionamento = new RandomAccessFile("EST.DAT", "r");
			while (true) {
				char at = arqEstacionamento.readChar();
				String cod = arqEstacionamento.readUTF();
				arqEstacionamento.readUTF(); // placa
				arqEstacionamento.readUTF(); // dataOperacao
				arqEstacionamento.readChar(); // tipoOperacao
				arqEstacionamento.readUTF(); // modeloCor
				arqEstacionamento.readUTF(); // marca
				arqEstacionamento.readUTF(); // categoriaVeiculo
				arqEstacionamento.readUTF(); // horaEntrada
				arqEstacionamento.readUTF(); // horaSaida
				arqEstacionamento.readFloat(); // valorPago

				try {
					int codInt = Integer.parseInt(cod);
					if (codInt > maiorCodEst) {
						maiorCodEst = codInt;
					}
				} catch (NumberFormatException e) {
					// Ignora
				}
			}
		} catch (EOFException e) {
			// Fim do arquivo
		} catch (FileNotFoundException e) {
			// Arquivo não existe ainda
		} catch (IOException e) {
			System.out.println("Erro ao ler o arquivo EST.DAT para auto-incremento.");
		}

		String codEstChave = String.format("%06d", maiorCodEst + 1);
		System.out.println("Código gerado para esta entrada: " + codEstChave);

		// Leitura e Validação da Placa
		do {
			System.out.print("Digite a placa do veículo no formato XXX0000 (ou FIM para cancelar): ");
			placa = Main.leia.nextLine().trim();

			if (placa.equalsIgnoreCase("FIM")) {
				System.out.println("Operação cancelada.");
				return;
			}

			if (placa.length() != 7) {
				System.out.println("Placa inválida! Deve conter exatamente 7 caracteres.");
			}
		} while (placa.length() != 7);

		// Leitura e Validação da Data
		do {
			System.out.print("Digite a data de entrada (DD/MM/AAAA): ");
			dataOperacao = Main.leia.nextLine().trim();
			if (!validarData(dataOperacao)) {
				System.out.println("Data inválida ou no formato incorreto! Use DD/MM/AAAA.");
			}
		} while (!validarData(dataOperacao));

		// Leitura do Modelo/Cor
		do {
			System.out.print("Digite o Modelo/Cor do veículo: ");
			modeloCor = Main.leia.nextLine().trim();
			if (modeloCor.isEmpty()) {
				System.out.println("Modelo/Cor não pode ficar em branco.");
			}
		} while (modeloCor.isEmpty());

		// Leitura e Validação da Marca (Sarah)
		int indiceMarca = -1;
		do {
			System.out.println("\nSiglas de marcas disponíveis:");
			for (int i = 0; i < COD_MARCA_VEIC.length; i++) {
				System.out.print("[" + COD_MARCA_VEIC[i] + "] " + DESCRICAO_MARCA[i] + "  ");
				if ((i + 1) % 5 == 0) System.out.println();
			}
			System.out.print("\nDigite a sigla da marca correspondente: ");
			String siglaDigitada = Main.leia.nextLine().trim().toUpperCase();

			indiceMarca = pesquisarMarcaVeic(siglaDigitada);
			if (indiceMarca == -1) {
				System.out.println("Sigla de marca inválida! Tente novamente.");
			} else {
				marca = DESCRICAO_MARCA[indiceMarca];
				System.out.println("Marca selecionada: " + marca);
			}
		} while (indiceMarca == -1);

		// Leitura e Validação da Categoria
		do {
			System.out.println("\nCategorias disponíveis:");
			System.out.println(" [GI] Grande Importado");
			System.out.println(" [PI] Pequeno Importado");
			System.out.println(" [GN] Grande Nacional");
			System.out.println(" [PN] Pequeno Nacional");
			System.out.print("Digite a sigla da categoria do veículo: ");
			categoriaVeiculo = Main.leia.nextLine().trim().toUpperCase();

			if (!categoriaVeiculo.equals("GI") && !categoriaVeiculo.equals("PI") && 
				!categoriaVeiculo.equals("GN") && !categoriaVeiculo.equals("PN")) {
				System.out.println("Categoria inválida! Escolha GI, PI, GN ou PN.");
			}
		} while (!categoriaVeiculo.equals("GI") && !categoriaVeiculo.equals("PI") && 
				 !categoriaVeiculo.equals("GN") && !categoriaVeiculo.equals("PN"));

		// Leitura e Validação da Hora de Entrada
		do {
			System.out.print("Digite a hora de entrada (HH:MM): ");
			horaEntrada = Main.leia.nextLine().trim();
			if (!validarHora(horaEntrada)) {
				System.out.println("Hora inválida! Digite no formato HH:MM de 00:00 até 23:59.");
			}
		} while (!validarHora(horaEntrada));

		// Inicializa campos de saída
		ativo = 'S';
		codEst = codEstChave;
		tipoOperacao = 'E';
		horaSaida = "";
		valorPago = 0.0f;

		// Confirmação de Entrada
		char confirmacao;
		do {
			System.out.print("Confirma a gravação dos dados de entrada (S/N)? ");
			String input = Main.leia.nextLine().trim().toUpperCase();
			confirmacao = input.isEmpty() ? ' ' : input.charAt(0);
		} while (confirmacao != 'S' && confirmacao != 'N');

		if (confirmacao == 'S') {
			salvarRegistroVeiculo();
			System.out.println("Entrada do veículo registrada com sucesso!\n");
		} else {
			System.out.println("Operação cancelada.\n");
		}
	}

	// *********************** 2. REGISTRAR SAÍDA DE VEÍCULO (Thiago) ***********************
	public void sairVeiculo() {
		System.out.println("\n--- REGISTRAR SAÍDA DE VEÍCULO ---");
		Main.leia.nextLine(); // Limpar buffer

		String codEstChave;
		long posicaoRegistro;

		do {
			System.out.print("Digite o código de estacionamento do veículo (ou FIM para cancelar): ");
			codEstChave = Main.leia.nextLine().trim();
			if (codEstChave.equalsIgnoreCase("FIM")) {
				System.out.println("Operação cancelada.");
				return;
			}

			posicaoRegistro = pesquisarRegistroVeiculo(codEstChave);
			if (posicaoRegistro == -1) {
				System.out.println("Código não cadastrado ou veículo inativo no arquivo. Tente novamente.");
			}
		} while (posicaoRegistro == -1);

		// Verifica se o veículo já registrou saída
		if (tipoOperacao == 'S') {
			System.out.println("Esse veículo já registrou saída anteriormente!");
			System.out.println("Código: " + codEst + " | Placa: " + placa + " | Saída registrada às: " + horaSaida);
			return;
		}

		// Exibir dados atuais do veículo
		System.out.println("\n--- DADOS DO VEÍCULO LOCALIZADO ---");
		System.out.println("Código: " + codEst);
		System.out.println("Placa: " + placa);
		System.out.println("Modelo e Cor: " + modeloCor);
		System.out.println("Marca: " + marca);
		System.out.println("Categoria: " + categoriaVeiculo);
		System.out.println("Data de Entrada: " + dataOperacao);
		System.out.println("Hora de Entrada: " + horaEntrada);
		System.out.println("-----------------------------------");

		// Solicitar e validar hora de saída
		String horaSaidaDigitada;
		int totalMinutosEntrada = 0;
		int totalMinutosSaida = 0;
		float totalHoras = 0;

		do {
			System.out.print("Digite a hora de saída do veículo (HH:MM): ");
			horaSaidaDigitada = Main.leia.nextLine().trim();
			if (!validarHora(horaSaidaDigitada)) {
				System.out.println("Formato de hora inválido! Digite no formato HH:MM (00:00 até 23:59)");
				continue;
			}

			// Converter horas para minutos
			int he = Integer.parseInt(horaEntrada.substring(0, 2));
			int me = Integer.parseInt(horaEntrada.substring(3, 5));
			int hs = Integer.parseInt(horaSaidaDigitada.substring(0, 2));
			int ms = Integer.parseInt(horaSaidaDigitada.substring(3, 5));

			totalMinutosEntrada = he * 60 + me;
			totalMinutosSaida = hs * 60 + ms;

			// Valida se a hora da saída é menor que a entrada
			if (totalMinutosSaida < totalMinutosEntrada) {
				System.out.println("Hora de saída não pode ser menor que a hora de entrada (" + horaEntrada + ")!");
				continue;
			}

			totalHoras = (totalMinutosSaida - totalMinutosEntrada) / 60.0f;
			break;
		} while (true);

		// Calcular valor a ser pago com base na categoria e horário (Thiago - corrigido)
		int he = Integer.parseInt(horaEntrada.substring(0, 2));
		int me = Integer.parseInt(horaEntrada.substring(3, 5));
		boolean ate18 = (he < 18) || (he == 18 && me == 0);
		String cat = categoriaVeiculo.toUpperCase();
		float vlrHora = 0;

		if (ate18) {
			// Tarifas Diurnas (Entrada até 18:00)
			if (cat.equals("GI")) {
				vlrHora = 10.0f;
			} else if (cat.equals("PI")) {
				vlrHora = 8.20f;
			} else if (cat.equals("GN")) {
				vlrHora = 9.0f;
			} else if (cat.equals("PN")) {
				vlrHora = 7.0f;
			}
		} else {
			// Tarifas Noturnas (Entrada após 18:00)
			if (cat.equals("GI")) {
				vlrHora = 8.0f;
			} else if (cat.equals("PI")) {
				vlrHora = 6.50f;
			} else if (cat.equals("GN")) {
				vlrHora = 9.0f;
			} else if (cat.equals("PN")) {
				vlrHora = 6.0f;
			}
		}

		float valorPagoCalculado = totalHoras * vlrHora;
		System.out.printf("\nTempo de Permanência: %.2f horas\n", totalHoras);
		System.out.printf("Valor da Tarifa por hora: R$ %.2f\n", vlrHora);
		System.out.printf("Valor Pago Total Calculado: R$ %.2f\n", valorPagoCalculado);

		// Confirmação da saída
		char confirmacao;
		do {
			System.out.print("\nConfirma a saída do veículo com este pagamento (S/N)? ");
			String input = Main.leia.nextLine().trim().toUpperCase();
			confirmacao = input.isEmpty() ? ' ' : input.charAt(0);
		} while (confirmacao != 'S' && confirmacao != 'N');

		if (confirmacao == 'S') {
			// Desativa o registro original de entrada
			desativarRegistroVeiculo(posicaoRegistro);

			// Prepara novos dados para gravar a saída
			ativo = 'S';
			tipoOperacao = 'S';
			horaSaida = horaSaidaDigitada;
			valorPago = valorPagoCalculado;

			// Salva o novo registro atualizado como Saída
			salvarRegistroVeiculo();
			System.out.println("Saída registrada com sucesso!\n");
		} else {
			System.out.println("Saída do veículo cancelada.\n");
		}
	}

	// *********************** 3. CONSULTA DE VEÍCULO ***********************
	public void consultarVeiculo() {
		Main.leia.nextLine(); // Limpar buffer
		byte opcaoConsulta;

		do {
			System.out.println("\n--- SUBMENU CONSULTAS ---");
			System.out.println(" [1] Pesquisar por Código de Estacionamento");
			System.out.println(" [2] Pesquisar por Placa do Veículo");
			System.out.println(" [3] Listar veículos atualmente estacionados");
			System.out.println(" [4] Listar histórico completo de registros");
			System.out.println(" [0] Voltar");
			System.out.print("Digite a opção: ");
			try {
				opcaoConsulta = Main.leia.nextByte();
			} catch (Exception e) {
				opcaoConsulta = -1;
			}
			Main.leia.nextLine(); // Limpar buffer

			switch (opcaoConsulta) {
				case 1:
					System.out.print("Digite o código de estacionamento (6 dígitos): ");
					String codPesq = Main.leia.nextLine().trim();
					long pos = pesquisarRegistroVeiculo(codPesq);
					if (pos == -1) {
						System.out.println("Registro ativo não encontrado para o código fornecido.");
					} else {
						exibirRegistroDetalhado();
					}
					break;

				case 2:
					System.out.print("Digite a placa do veículo (7 caracteres): ");
					String placaPesq = Main.leia.nextLine().trim().toUpperCase();
					pesquisarPorPlaca(placaPesq);
					break;

				case 3:
					listarEstacionados();
					break;

				case 4:
					listarHistoricoCompleto();
					break;

				case 0:
					break;

				default:
					System.out.println("Opção inválida!");
			}
		} while (opcaoConsulta != 0);
	}

	private void exibirRegistroDetalhado() {
		System.out.println("\n--- DETALHES DO REGISTRO ---");
		System.out.println("Código Estacionamento: " + codEst);
		System.out.println("Placa: " + placa);
		System.out.println("Data Operação: " + dataOperacao);
		System.out.println("Operação: " + (tipoOperacao == 'E' ? "Entrada" : "Saída"));
		System.out.println("Modelo/Cor: " + modeloCor);
		System.out.println("Marca: " + marca);
		System.out.println("Categoria: " + categoriaVeiculo);
		System.out.println("Hora Entrada: " + horaEntrada);
		System.out.println("Hora Saída: " + (horaSaida.isEmpty() ? "Em permanência" : horaSaida));
		System.out.printf("Valor Pago: R$ %.2f\n", valorPago);
		System.out.println("----------------------------");
	}

	private void pesquisarPorPlaca(String placaPesq) {
		boolean achou = false;
		try {
			RandomAccessFile arqEstacionamento = new RandomAccessFile("EST.DAT", "r");
			while (true) {
				char at = arqEstacionamento.readChar();
				String cod = arqEstacionamento.readUTF();
				String pl = arqEstacionamento.readUTF();
				String dt = arqEstacionamento.readUTF();
				char tp = arqEstacionamento.readChar();
				String mc = arqEstacionamento.readUTF();
				String ma = arqEstacionamento.readUTF();
				String cat = arqEstacionamento.readUTF();
				String he = arqEstacionamento.readUTF();
				String hs = arqEstacionamento.readUTF();
				float vp = arqEstacionamento.readFloat();

				if (pl.equalsIgnoreCase(placaPesq) && at == 'S') {
					if (!achou) {
						imprimirCabecalho();
						achou = true;
					}
					imprimirLinhaTabela(cod, pl, dt, tp, ma, cat, he, hs, vp);
				}
			}
		} catch (EOFException e) {
			// Fim de arquivo
		} catch (FileNotFoundException e) {
			System.out.println("Arquivo de dados EST.DAT ainda não existe.");
		} catch (IOException e) {
			System.out.println("Erro na leitura do arquivo - " + e.getMessage());
		}

		if (!achou) {
			System.out.println("Nenhum registro ativo encontrado para a placa: " + placaPesq);
		}
	}

	private void listarEstacionados() {
		boolean achou = false;
		try {
			RandomAccessFile arqEstacionamento = new RandomAccessFile("EST.DAT", "r");
			while (true) {
				char at = arqEstacionamento.readChar();
				String cod = arqEstacionamento.readUTF();
				String pl = arqEstacionamento.readUTF();
				String dt = arqEstacionamento.readUTF();
				char tp = arqEstacionamento.readChar();
				String mc = arqEstacionamento.readUTF();
				String ma = arqEstacionamento.readUTF();
				String cat = arqEstacionamento.readUTF();
				String he = arqEstacionamento.readUTF();
				String hs = arqEstacionamento.readUTF();
				float vp = arqEstacionamento.readFloat();

				// Veículos atualmente estacionados são os ativos ('S') com tipoOperacao 'E' (Entrada)
				if (at == 'S' && tp == 'E') {
					if (!achou) {
						imprimirCabecalho();
						achou = true;
					}
					imprimirLinhaTabela(cod, pl, dt, tp, ma, cat, he, hs, vp);
				}
			}
		} catch (EOFException e) {
			// Fim de arquivo
		} catch (FileNotFoundException e) {
			System.out.println("Arquivo de dados EST.DAT ainda não existe.");
		} catch (IOException e) {
			System.out.println("Erro na leitura do arquivo - " + e.getMessage());
		}

		if (!achou) {
			System.out.println("Nenhum veículo atualmente estacionado.");
		}
	}

	private void listarHistoricoCompleto() {
		boolean achou = false;
		try {
			RandomAccessFile arqEstacionamento = new RandomAccessFile("EST.DAT", "r");
			while (true) {
				char at = arqEstacionamento.readChar();
				String cod = arqEstacionamento.readUTF();
				String pl = arqEstacionamento.readUTF();
				String dt = arqEstacionamento.readUTF();
				char tp = arqEstacionamento.readChar();
				String mc = arqEstacionamento.readUTF();
				String ma = arqEstacionamento.readUTF();
				String cat = arqEstacionamento.readUTF();
				String he = arqEstacionamento.readUTF();
				String hs = arqEstacionamento.readUTF();
				float vp = arqEstacionamento.readFloat();

				if (at == 'S') {
					if (!achou) {
						imprimirCabecalho();
						achou = true;
					}
					imprimirLinhaTabela(cod, pl, dt, tp, ma, cat, he, hs, vp);
				}
			}
		} catch (EOFException e) {
			// Fim de arquivo
		} catch (FileNotFoundException e) {
			System.out.println("Arquivo de dados EST.DAT ainda não existe.");
		} catch (IOException e) {
			System.out.println("Erro na leitura do arquivo - " + e.getMessage());
		}

		if (!achou) {
			System.out.println("Nenhum registro ativo cadastrado.");
		}
	}

	public void imprimirCabecalho() {
		System.out.println("\n" + 
			formatarString("CÓDIGO", 8) + " " +
			formatarString("PLACA", 9) + " " +
			formatarString("DATA", 12) + " " +
			formatarString("OP", 4) + " " +
			formatarString("MARCA", 15) + " " +
			formatarString("CAT", 5) + " " +
			formatarString("H_ENT", 7) + " " +
			formatarString("H_SAI", 7) + " " +
			formatarString("VLR_PAGO", 10)
		);
		System.out.println("==================================================================================");
	}

	public void imprimirLinhaTabela(String cod, String pl, String dt, char tp, String ma, String cat, String he, String hs, float vp) {
		System.out.println(
			formatarString(cod, 8) + " " +
			formatarString(pl, 9) + " " +
			formatarString(dt, 12) + " " +
			formatarString(Character.toString(tp), 4) + " " +
			formatarString(ma, 15) + " " +
			formatarString(cat, 5) + " " +
			formatarString(he, 7) + " " +
			formatarString(hs.isEmpty() ? "-" : hs, 7) + " " +
			formatarString(String.format("R$ %.2f", vp), 10)
		);
	}

	public static String formatarString(String texto, int tamanho) {
		if (texto.length() > tamanho) {
			texto = texto.substring(0, tamanho);
		} else {
			while (texto.length() < tamanho) {
				texto = texto + " ";
			}
		}
		return texto;
	}

	// *********************** 4. EXCLUSÃO DE VEÍCULO ***********************
	public void excluirVeiculo() {
		System.out.println("\n--- EXCLUIR REGISTRO DE VEÍCULO ---");
		Main.leia.nextLine(); // Limpar buffer
		System.out.print("Digite o código de estacionamento do registro a ser excluído: ");
		String codPesq = Main.leia.nextLine().trim();

		long pos = pesquisarRegistroVeiculo(codPesq);
		if (pos == -1) {
			System.out.println("Registro ativo não encontrado para o código fornecido.");
			return;
		}

		exibirRegistroDetalhado();

		char confirmacao;
		do {
			System.out.print("Tem certeza que deseja excluir permanentemente este registro (S/N)? ");
			String input = Main.leia.nextLine().trim().toUpperCase();
			confirmacao = input.isEmpty() ? ' ' : input.charAt(0);
		} while (confirmacao != 'S' && confirmacao != 'N');

		if (confirmacao == 'S') {
			desativarRegistroVeiculo(pos);
			System.out.println("Registro excluído logicamente com sucesso!\n");
		} else {
			System.out.println("Exclusão cancelada.\n");
		}
	}

	// *********************** 5. EMITIR FATURAMENTO ***********************
	public void emitirFaturamento() {
		System.out.println("\n--- RELATÓRIO DE FATURAMENTO ---");
		int totalAtivoEstacionados = 0;
		int totalSaidasRegistradas = 0;
		float faturamentoTotal = 0.0f;

		// Faturamento detalhado por categoria
		float fatGI = 0.0f, fatPI = 0.0f, fatGN = 0.0f, fatPN = 0.0f;
		int countGI = 0, countPI = 0, countGN = 0, countPN = 0;

		try {
			RandomAccessFile arqEstacionamento = new RandomAccessFile("EST.DAT", "r");
			while (true) {
				char at = arqEstacionamento.readChar();
				String cod = arqEstacionamento.readUTF();
				arqEstacionamento.readUTF(); // placa
				arqEstacionamento.readUTF(); // dataOperacao
				char tp = arqEstacionamento.readChar();
				arqEstacionamento.readUTF(); // modeloCor
				arqEstacionamento.readUTF(); // marca
				String cat = arqEstacionamento.readUTF();
				arqEstacionamento.readUTF(); // horaEntrada
				arqEstacionamento.readUTF(); // horaSaida
				float vp = arqEstacionamento.readFloat();

				if (at == 'S') {
					if (tp == 'E') {
						totalAtivoEstacionados++;
					} else if (tp == 'S') {
						totalSaidasRegistradas++;
						faturamentoTotal += vp;

						// Classificar faturamento
						if (cat.equalsIgnoreCase("GI")) {
							fatGI += vp;
							countGI++;
						} else if (cat.equalsIgnoreCase("PI")) {
							fatPI += vp;
							countPI++;
						} else if (cat.equalsIgnoreCase("GN")) {
							fatGN += vp;
							countGN++;
						} else if (cat.equalsIgnoreCase("PN")) {
							fatPN += vp;
							countPN++;
						}
					}
				}
			}
		} catch (EOFException e) {
			// Fim de arquivo
		} catch (FileNotFoundException e) {
			System.out.println("Arquivo de dados EST.DAT ainda não existe.");
			return;
		} catch (IOException e) {
			System.out.println("Erro na leitura do arquivo para faturamento - " + e.getMessage());
			return;
		}

		System.out.println("\n=============================================");
		System.out.println(" RESUMO GERAL DE MOVIMENTAÇÕES:");
		System.out.println("=============================================");
		System.out.println(" Veículos Ativos Estacionados: " + totalAtivoEstacionados);
		System.out.println(" Saídas de Veículos Registradas: " + totalSaidasRegistradas);
		System.out.printf(" Receita Total Acumulada: R$ %.2f\n", faturamentoTotal);
		System.out.println("=============================================");
		System.out.println(" DETALHAMENTO DE FATURAMENTO POR CATEGORIA:");
		System.out.println("=============================================");
		System.out.printf(" Grande Importado (GI)  : %d saídas | Faturamento: R$ %.2f\n", countGI, fatGI);
		System.out.printf(" Pequeno Importado (PI) : %d saídas | Faturamento: R$ %.2f\n", countPI, fatPI);
		System.out.printf(" Grande Nacional (GN)   : %d saídas | Faturamento: R$ %.2f\n", countGN, fatGN);
		System.out.printf(" Pequeno Nacional (PN)  : %d saídas | Faturamento: R$ %.2f\n", countPN, fatPN);
		System.out.println("=============================================\n");

		// Espera pressionar Enter para voltar
		System.out.println("Pressione ENTER para voltar ao menu principal...");
		try {
			Main.leia.nextLine();
		} catch (Exception e) {}
	}
}
