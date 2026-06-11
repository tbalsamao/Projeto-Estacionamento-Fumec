import java.io.*;

public class Estacionamento {

	char 	ativo;
	String	codEst; // Chave Primária, que indica qual é a entrada do veículo. Um mesmo veículo pode ter vários codEst.
	String 	placa; // XXX9999
	String 	dataOperacao; // DD/MM/AAAA
	char    tipoOperacao;  // E ou S, entrada ou saída
	String  modeloCor; // não foi explicado
	String  marca; // Tabela de marcas para escolher
	String  categoriaVeiculo; // Tabela de categorias para escolher
	String  horaEntrada; // Hora de entrada
	String  horaSaida; // Hora de saída
	float   valorPago; // Valor pago baseado no tempo de permanência e na categoria do veículo

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
		// metodo para alterar o valor do campo ATIVO para N, tornando assim o registro excluido
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
		String codEstChave = "000000";
		int maiorCodEst = 0;
		char confirmacao;
		long posicaoRegistro;
		
		do {
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
			codEst =codEstChave;
			System.out.println("Digite a placa do veículo no formato XXX0000 (FIM para encerrar): ");
			placa            = Main.leia.nextLine();
			if (placa.equalsIgnoreCase("FIM")) {
				break;
			}
			System.out.println("Digite a data de entrada do veículo: ");
			dataOperacao     = Main.leia.nextLine();
			tipoOperacao     = 'E';
			System.out.println("Digite o Modelo/Cor do veículo: ");
			modeloCor        = Main.leia.nextLine();
			System.out.println("Digite a marca do veículo: ");
			marca            = Main.leia.nextLine();
			System.out.println("Digite a categoria do veículo: ");
			categoriaVeiculo = Main.leia.nextLine();
			System.out.println("Digite a hora de entrada do veículo: ");
			horaEntrada      = Main.leia.nextLine();
			horaSaida        = "";
			valorPago        = 0;
		} while (!placa.equals("FIM"));
		
		// Continuar a partir da validação
		
		
	}


	//************************  ALTERACAO  *****************************
//	public void alterar() {
//		String matriculaChave;
//		char confirmacao;
//		long posicaoRegistro = 0;
//		byte opcao;
//
//		do {
//			do {
//				Main.leia.nextLine();
//				System.out.println("\n ***************  ALTERACAO DE ALUNOS  ***************** ");
//				System.out.print("Digite a Matricula do Aluno que deseja alterar( FIM para encerrar ): ");
//				matriculaChave = Main.leia.nextLine();
//				if (matriculaChave.equals("FIM")) {
//					break;
//				}
//
//				posicaoRegistro = pesquisarAluno(matriculaChave);
//				if (posicaoRegistro == -1) {
//					System.out.println("Matricula nao cadastrada no arquivo, digite outro valor\n");
//				}
//			}while (posicaoRegistro == -1);
//
//			if (matriculaChave.equals("FIM")) {
//				break;
//			}
//
//			ativo = 'S';
//
//			do {
//				System.out.println("[ 1 ] Nome do Aluno............: " + nomeAluno);
//				System.out.println("[ 2 ] Data de nascimento ......: " + dtNasc);
//				System.out.println("[ 3 ] Valor da mensalidade.....: " + mensalidade);
//				System.out.println("[ 4 ] sexo do Aluno............: " + sexo);
//
//				do{
//					System.out.println("Digite o numero do campo que deseja alterar (0 para finalizar as alterações): ");
//					opcao = Main.leia.nextByte();
//				}while (opcao < 0 || opcao > 4);
//
//				switch (opcao) {
//				case 1:
//					Main.leia.nextLine();
//					System.out.print  ("Digite o NOVO NOME do Aluno..................: ");
//					nomeAluno = Main.leia.nextLine();
//					break;
//				case 2: 
//					Main.leia.nextLine();
//					System.out.print  ("Digite a NOVA DATA de Nascimento (DD/MM/AAAA): ");
//					dtNasc = Main.leia.nextLine();
//					break;
//				case 3:
//					System.out.print  ("Digite o NOVO VALOR da mensalidade...........: ");
//					mensalidade = Main.leia.nextFloat();
//					break;
//				case 4: 
//					System.out.print  ("Digite o NOVO sexo do Aluno (M/F)............: ");
//					sexo = Main.leia.next().charAt(0);
//					break;
//				}
//				System.out.println();
//			}while (opcao != 0);  		
//
//			do {
//				System.out.print("\nConfirma a alteracao dos dados (S/N) ? ");
//				confirmacao = Main.leia.next().charAt(0);
//				if (confirmacao == 'S') {
//					desativarAluno(posicaoRegistro);
//					salvarAluno();
//					System.out.println("Dados gravados com sucesso !\n");
//				}
//			}while (confirmacao != 'S' && confirmacao != 'N');
//
//		}while ( ! matricula.equals("FIM"));
//	}
//
//
//	//************************  EXCLUSAO  *****************************
//	public void excluir() {
//		String matriculaChave;
//		char confirmacao;
//		long posicaoRegistro = 0;
//
//		do {
//			do {
//				Main.leia.nextLine();
//				System.out.println(" ***************  EXCLUSAO DE ALUNOS  ***************** ");
//				System.out.print("Digite a Matricula do Aluno que deseja excluir ( FIM para encerrar ): ");
//				matriculaChave = Main.leia.nextLine();
//				if (matriculaChave.equals("FIM")) {
//					break;
//				}
//
//				posicaoRegistro = pesquisarAluno(matriculaChave);
//				if (posicaoRegistro == -1) {
//					System.out.println("Matricula nao cadastrada no arquivo, digite outro valor\n");
//				}
//			}while (posicaoRegistro == -1);
//
//			if (matriculaChave.equals("FIM")) {
//				System.out.println("\n ************  PROGRAMA ENCERRADO  ************** \n");
//				break;
//			}
//
//			System.out.println("Nome do aluno.......: " + nomeAluno);
//			System.out.println("Data de nascimento..: " + dtNasc);
//			System.out.println("Valor da mensalidade: " + mensalidade);
//			System.out.println("Sexo do aluno.......: " + sexo);
//			System.out.println();
//
//			do {
//				System.out.print("\nConfirma a exclusao deste aluno (S/N) ? ");
//				confirmacao = Main.leia.next().charAt(0);
//				if (confirmacao == 'S') {
//					desativarAluno(posicaoRegistro);
//				}
//			}while (confirmacao != 'S' && confirmacao != 'N');
//
//		}while ( ! matricula.equals("FIM"));
//	}
//
//	//************************  CONSULTA  *****************************
//	public void consultar() 	{
//		RandomAccessFile arqEstacionamento;
//		byte opcao;
//		String matriculaChave;
//		char sexoAux;
//		long posicaoRegistro;
//
//		do {
//			do {
//				System.out.println(" ***************  CONSULTA DE ALUNOS  ***************** ");
//				System.out.println(" [1] CONSULTAR APENAS 1 ALUNO ");
//				System.out.println(" [2] LISTA DE TODOS OS ALUNOS ");
//				System.out.println(" [3] LISTA SOMENTE SEXO MASCULINO OU FEMININO ");
//				System.out.println(" [0] SAIR");
//				System.out.print("\nDigite a opcao desejada: ");
//				opcao = Main.leia.nextByte();
//				if (opcao < 0 || opcao > 3) {
//					System.out.println("opcao Invalida, digite novamente.\n");
//				}
//			}while (opcao < 0 || opcao > 3);
//
//			switch (opcao) {
//			case 0:
//				System.out.println("\n ************  PROGRAMA ENCERRADO  ************** \n");
//				break;
//
//			case 1:  // consulta de uma unica matricula
//				Main.leia.nextLine();  // limpa buffer de memoria
//				System.out.print("Digite a Matriocula do Aluno: ");
//				matriculaChave = Main.leia.nextLine();
//
//				posicaoRegistro = pesquisarAluno(matriculaChave);
//				if (posicaoRegistro == -1) {
//					System.out.println("Matricula nao cadastrada no arquivo \n");
//				} else {
//					imprimirCabecalho();
//					imprimirAluno();
//					System.out.println("\n FIM DE RELATORIO - ENTER para continuar...\n");
//					Main.leia.nextLine();
//				}
//
//				break;
//
//			case 2:  // imprime todos os alunos
//				try { 
//					arqEstacionamento = new RandomAccessFile("ALUNO.DAT" , "rw");
//					imprimirCabecalho();
//					while (true) {
//						ativo		= arqEstacionamento.readChar();
//						matricula   = arqEstacionamento.readUTF();
//						nomeAluno   = arqEstacionamento.readUTF();
//						dtNasc      = arqEstacionamento.readUTF();
//						mensalidade = arqEstacionamento.readFloat();
//						sexo        = arqEstacionamento.readChar();
//						if ( ativo == 'S') {
//							imprimirAluno();
//						}
//					}
//					//    arqEstacionamento.close();
//				} catch (EOFException e) {
//					System.out.println("\n FIM DE RELATORIO - ENTER para continuar...\n");
//					Main.leia.nextLine();
//					matriculaChave = Main.leia.nextLine();
//				} catch (IOException e) { 
//					System.out.println("Erro na abertura do arquivo - programa sera finalizado");
//					System.exit(0);
//				}
//				break;
//
//			case 3:  // imprime alunos do sexo desejado
//				do {
//					System.out.print("Digite o Sexo desejado (M/F): ");
//					sexoAux = Main.leia.next().charAt(0);
//					if (sexoAux != 'F' && sexoAux != 'M') {
//						System.out.println("Sexo Invalido, digite M ou F");
//					}
//				}while (sexoAux != 'F' && sexoAux != 'M');
//
//				try { 
//					arqEstacionamento = new RandomAccessFile("ALUNO.DAT", "rw");
//					imprimirCabecalho();
//					while (true) {
//						ativo		= arqEstacionamento.readChar();
//						matricula   = arqEstacionamento.readUTF();
//						nomeAluno   = arqEstacionamento.readUTF();
//						dtNasc      = arqEstacionamento.readUTF();
//						mensalidade = arqEstacionamento.readFloat();
//						sexo        = arqEstacionamento.readChar();
//
//						if ( sexoAux == sexo && ativo == 'S') {
//							imprimirAluno();
//						}
//					}
//				} catch (EOFException e) {
//					System.out.println("\n FIM DE RELATORIO - ENTER para continuar...\n");
//					Main.leia.nextLine();
//					matriculaChave = Main.leia.nextLine();
//				} catch (IOException e) { 
//					System.out.println("Erro na abertura do arquivo - programa sera finalizado");
//					System.exit(0);
//				}
//
//			}	
//
//		} while ( opcao != 0 );
//	}
//
//	public void imprimirCabecalho () {
//		System.out.println("-MATRICULA-  -------- NOME ALUNO ----------  --DATA NASC--  -Mensalidade-  -sexo- ");
//	}
//
//	public void imprimirAluno () {
//		System.out.println(	formatarString(matricula, 11 ) + "  " +
//				formatarString(nomeAluno , 30) + "  " + 
//				formatarString(dtNasc , 13) + "  " + 
//				formatarString( String.valueOf(mensalidade) , 13 ) + "  " +
//				formatarString( Character.toString(sexo) , 6 )   ); 
//	}
//
//	public static String formatarString (String texto, int tamanho) {	
//		// retorna uma string com o numero de caracteres passado como parametro em TAMANHO
//		if (texto.length() > tamanho) {
//			texto = texto.substring(0,tamanho);
//		}else{
//			while (texto.length() < tamanho) {
//				texto = texto + " ";
//			}
//		}
//		return texto;
//	}
}
