import java.util.*;

public class Main {
	// Scanner estático compartilhado com a classe Estacionamento
	public static Scanner leia = new Scanner(System.in);

	public static void main(String[] args) {	
		Estacionamento estacionamento = new Estacionamento();
		byte opcao = -1;
		 
		do {
			do {
				System.out.println("\n ***************  Controle de Estacionamento  ***************** ");
				System.out.println(" [1] ENTRADA DE VEÍCULO ");
				System.out.println(" [2] SAÍDA DE VEÍCULO ");
				System.out.println(" [3] CONSULTA DE VEÍCULO ");
				System.out.println(" [4] EXCLUSÃO DE VEÍCULO ");
				System.out.println(" [5] RELATÓRIO DE FATURAMENTO ");
				System.out.println(" [0] SAIR");
				System.out.print("\nDigite a opcao desejada: ");
				
				try {
					opcao = leia.nextByte();
					if (opcao < 0 || opcao > 5) {
						System.out.println("Opção Inválida, digite novamente.\n");
					}
				} catch (InputMismatchException e) {
					System.out.println("Entrada inválida! Digite um número inteiro correspondente às opções.");
					leia.nextLine(); // Limpa buffer do scanner
					opcao = -1;
				}
			} while (opcao < 0 || opcao > 5);
			
			switch (opcao) {
				case 0:
					System.out.println("\n ************  PROGRAMA ENCERRADO  ************** \n");
					break;
				case 1: 
					estacionamento.entrarVeiculo(); 
					break;
				case 2:
					estacionamento.sairVeiculo();
					break;
				case 3: 
					estacionamento.consultarVeiculo();
					break;
				case 4: 
					estacionamento.excluirVeiculo();
					break;
				case 5:
					estacionamento.emitirFaturamento();
					break;
			}
		} while (opcao != 0);
		
		leia.close();
	}
}
