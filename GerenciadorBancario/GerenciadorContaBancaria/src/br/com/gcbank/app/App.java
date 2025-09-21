package br.com.gcbank.app;

import java.util.Scanner;
import br.com.gcbank.service.ContaService;
public class App {
    public static void main(String[] args) {
        ContaService service = new ContaService();
        try (Scanner sc = new Scanner(System.in)) {
            int option;
            do {
                menu();
                option = lerInt(sc, "Opção: ");
                switch (option) {
                    case 1 -> service.listar();
                    case 2 -> {
                        int num = lerInt(sc, "No.: ");
                        String tit = lerLinha(sc, "Titular: ");
                        double saldo = lerDouble(sc, "Saldo: ");
                        service.inserir(num, tit, saldo);
                    }
                    case 3 -> {
                        int num = lerInt(sc, "Conta: ");
                        double val = lerDouble(sc, "Depósito: ");
                        service.depositar(num, val);
                    }
                    case 4 -> {
                        int num = lerInt(sc, "Conta: ");
                        double val = lerDouble(sc, "Saque: ");
                        service.sacar(num, val);
                    }
                    case 5 -> {
                        int num = lerInt(sc, "Excluir conta: ");
                        service.excluir(num);
                    }
                    case 6 -> {
                        int o = lerInt(sc, "Origem: ");
                        int d = lerInt(sc, "Destino: ");
                        double v = lerDouble(sc, "Valor: ");
                        service.transferir(o, d, v);
                    }
                    case 7 -> service.filtrar(ContaService.SALDO_MAIOR_5000);
                    case 8 -> service.filtrar(ContaService.NUMERO_PAR);
                    case 9 -> service.recarregar();
                    case 0 -> System.out.println("Saindo...");
                    default -> System.out.println("Opção inválida.");
                }
            } while (option != 0);
        }
    }

    private static void menu() {
        System.out.println("""

        ------ Gerenciador Bancario ------
        1) Listar contas
        2) Inserir uma nova conta
        3) Depositar um valor
        4) Sacar um valor
        5) Excluir conta
        6) Transferir um valor
        7) Filtrar contas com saldo > 5000
        8) Filtrar contas com no. pares
        9) Atualizar base
        0) Sair e fechar
                
                """);
    }


    //Utilitarios

    private static int lerInt(Scanner sc, String msg) {
        System.out.print(msg);
        while (!sc.hasNextInt()) {
            System.out.print("Erro: Valor inválido. " + msg);
            sc.next();
        }
        int v = sc.nextInt(); sc.nextLine();
        return v;
    }

    private static double lerDouble(Scanner sc, String msg) {
        System.out.print(msg);
        while (true) {
            String s = sc.nextLine().trim().replace(",", ".");
            try { return Double.parseDouble(s); }
            catch (NumberFormatException e) { System.out.print("Erro: Valor inválido. " + msg); }
        }
    }

    private static String lerLinha(Scanner sc, String msg) {
        System.out.print(msg);
        return sc.nextLine();
    }
}
