package aulafacul;
import java.util.*;
public class Main {
	static Scanner leia = new Scanner(System.in);
	
	public static void main(String[] args) {	
		estacionamento estacionamento = new estacionamento();
    	byte opcao = -1;
    	 
    	do {
			do {
    			System.out.println("\n *****  Controle de Estacionamento  ******* ");
    			System.out.println(" [1] ENTRADA DE VEÍCULO ");
    			System.out.println(" [2] SAÍDA DE VEÍCULO ");
    			System.out.println(" [3] CONSULTA DE VEÍCULO ");
    			System.out.println(" [4] EXCLUSÃO DE VEÍCULO ");
    			System.out.println(" [5] RELATÓRIO DE FATURAMENTO ");
    			System.out.println(" [0] SAIR");
    			System.out.print("\nDigite a opcao desejada: ");
    			opcao = leia.nextByte();
    			if (opcao < 0 || opcao > 5) {
    				System.out.println("Opção Invalida, digite novamente.\n");
    			}
    		}while (opcao < 0 || opcao > 5);
			
			switch (opcao) {
				case 0:
					System.out.println("\n ****  PROGRAMA ENCERRADO  ****** \n");
					break;
				case 1: 
					estacionamento.entrarVeiculo(); 
					break;
				case 2:
					//estacionamento.sairVeiculo();
					break;
				case 3: 
					//estacionamento.consultarVeiculo();
					break;
				case 4: 
					//estacionamento.excluirVeiculo();
					break;
				case 5:
					//estacionamento.emitirFaturamento();
			}
    	} while ( opcao != 0 );
    	leia.close();
	}
}