package br.com.gcbank.app;

import java.util.Scanner;

import br.com.gcbank.model.ContaCorrente;
import br.com.gcbank.service.ContaService;
import br.com.gcbank.service.TarifaService;
import br.com.gcbank.strategy.TarifaStrategy;
public class App {
    public static void main(String[] args) {
        ContaService service = new ContaService();

        service.recarregar(); // carrega as contas do banco
        service.listar(); // imprime


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
                    case 7 -> service.imprimirFiltradas(ContaService.SALDO_MAIOR_5000);
                    case 8 -> service.imprimirFiltradas(ContaService.NUMERO_PAR);
                    case 9 -> service.listarOrdenadoPorSaldo();
                    case 10 -> service.listarOrdenadoPorTitular();
                    case 11 -> {
                        int numero = lerInt(sc, "Número da conta: ");
                        
                        TarifaStrategy strategy = TarifaStrategy.ISENTA;
                        
                        // Escolher estratégia


                        System.out.println("Escolha a estratégia de tarifa:");
                        System.out.println("1) FIXA");
                        System.out.println("2) PERCENTUAL");
                        System.out.println("3) ISENTA");
                        int opc = lerInt(sc, "Opção: ");

                        switch(opc) {
                            case 1:
                                strategy = TarifaStrategy.FIXA;
                                break;
                            case 2:
                                strategy = TarifaStrategy.PERCENTUAL;
                                break;
                            case 3:
                                strategy = TarifaStrategy.ISENTA;
                                break;
                            default:
                                System.out.println("Opção inválida. Usando ISENTA por padrão.");
                        }

                        ContaCorrente c = service.buscarConta(numero);
                        if (c != null) {
                            double tarifa = TarifaService.aplicarTarifa(c, strategy);
                            System.out.printf("Tarifa aplicada: %.2f | Novo saldo: %.2f%n", tarifa, c.getSaldo());
                        } else {
                            System.out.println("Erro: Conta não encontrada.");
                        }

                        service.aplicarTarifaNaConta(c, strategy);
                    }                    
                    case 12 -> service.recarregar();
                    case 0 -> System.out.println("Saindo...");
                    default -> System.out.println("Erro: Opção inválida.");
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
        9) Ordenar p/ saldo decrescente
        10) Ordenar p/ ordem alfábetica
        11) Aplicar tarifa a uma conta
        12) Atualizar base
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
