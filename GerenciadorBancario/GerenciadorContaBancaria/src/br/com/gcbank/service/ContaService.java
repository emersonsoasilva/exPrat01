package br.com.gcbank.service;

import br.com.gcbank.banco.contaDAO;
import br.com.gcbank.exceptions.SaldoInsuficienteException;
import br.com.gcbank.model.ContaCorrente;
import br.com.gcbank.strategy.TarifaStrategy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
// import java.util.stream.Collectors;

public class ContaService {

    private final List<ContaCorrente> contas = new ArrayList<>();
    private final contaDAO dao;

    public static final Predicate<ContaCorrente> SALDO_MAIOR_5000 = c -> c.getSaldo() > 5000;
    public static final Predicate<ContaCorrente> NUMERO_PAR = c -> c.getNumero() % 2 == 0;

    public static final Comparator<ContaCorrente> POR_SALDO_DESC =
        Comparator.comparingDouble(ContaCorrente::getSaldo).reversed();

    public static final Comparator<ContaCorrente> POR_TITULAR =
        Comparator.comparing(ContaCorrente::getTitular, String.CASE_INSENSITIVE_ORDER);

    public ContaService() {
        this.dao = new contaDAO();
        recarregar();
        try {
            contas.addAll(dao.listar());
        } catch (SQLException e) {
            System.out.println("Aviso: Não foi possível carregar contas do banco: " + e.getMessage());
        }
    }

    public void listar() {
        if (contas.isEmpty()) {
            System.out.println("Nenhuma conta carregada!");
            return;
        }
        contas.stream()
              .sorted(Comparator.comparingInt(ContaCorrente::getNumero))
              .forEach(c -> System.out.printf(
                  "No. %d || %-20s || %.2f%n",
                  c.getNumero(), c.getTitular(), c.getSaldo()));
    }

    public void inserir(int numero, String titular, double saldo) {
        if (titular == null || titular.isBlank()) {
            System.out.println("Erro: Titular não pode ser vazio");
            return;
        }
        if (contas.stream().anyMatch(c -> c.getNumero() == numero)) {
            System.out.println("Erro: Conta já existe na memória!");
            return;
        }
        ContaCorrente nova = new ContaCorrente(numero, titular, saldo);
        try {
            dao.inserir(nova);
            contas.add(nova);
            System.out.println("Conta adicionada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao inserir: " + e.getMessage());
        }
    }

    public void depositar(int numero, double valor) {
        Optional<ContaCorrente> opt = contas.stream()
                                            .filter(c -> c.getNumero() == numero)
                                            .findFirst();
        if (opt.isEmpty()) {
            System.out.println("Conta não encontrada na memória!");
            return;
        }
        ContaCorrente conta = opt.get();
        try {
            conta.depositar(valor);
            dao.atualizarSaldo(numero, conta.getSaldo());
            System.out.println("Depósito realizado. Saldo: " + conta.getSaldo());
        } catch (IllegalArgumentException | SQLException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void sacar(int numero, double valor) {
        Optional<ContaCorrente> opt = contas.stream()
                                            .filter(c -> c.getNumero() == numero)
                                            .findFirst();
        if (opt.isEmpty()) {
            System.out.println("Conta não encontrada!");
            return;
        }
        ContaCorrente conta = opt.get();
        try {
            conta.sacar(valor);
            dao.atualizarSaldo(numero, conta.getSaldo());
            System.out.println("Saque realizado. Saldo: " + conta.getSaldo());
        } catch (SaldoInsuficienteException | IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erro no banco: " + e.getMessage());
        }
    }

    public void excluir(int numero) {
        try {
            if (dao.remover(numero)) {
                contas.removeIf(c -> c.getNumero() == numero);
                System.out.println("Conta excluída com sucesso!");
            } else {
                System.out.println("Conta não encontrada na base.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao excluir: " + e.getMessage());
        }
    }

    public void transferir(int origem, int destino, double valor) {
        try {
            dao.transferir(origem, destino, valor);

            // Atualiza na memória (opcional, para refletir o novo saldo sem recarregar tudo)
            Optional<ContaCorrente> cOrigem = contas.stream().filter(c -> c.getNumero() == origem).findFirst();
            Optional<ContaCorrente> cDestino = contas.stream().filter(c -> c.getNumero() == destino).findFirst();
            cOrigem.ifPresent(c -> c.setSaldo(c.getSaldo() - valor));
            cDestino.ifPresent(c -> c.setSaldo(c.getSaldo() + valor));

            System.out.println("Aviso: Transferência concluída!");
        } catch (SaldoInsuficienteException | IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erro no banco: " + e.getMessage());
        }
    }

    // Retorna a lista filtrada (não imprime)

    // contas.stream()
    //     .filter(saldoMaior5000)
    //     .forEach(c -> {
    //       // aplica tarifa em memória
    //       double tarifa = TarifaService.aplicarTarifa(c, TarifaStrategy.PERCENTUAL);
          
    //       // atualiza saldo no banco
    //       try {
    //           dao.atualizarSaldo(c.getNumero(), c.getSaldo());
    //           System.out.printf("Tarifa %.2f aplicada à conta %d | Novo saldo %.2f%n",
    //                             tarifa, c.getNumero(), c.getSaldo());
    //       } catch (SQLException e) {
    //           System.out.println("Erro ao atualizar conta " + c.getNumero() + ": " + e.getMessage());
    //       }
    //     });

    public List<ContaCorrente> filtrar(Predicate<ContaCorrente> filtro) {
        return contas.stream()
                     .filter(filtro)
                     .toList(); //  ou .collect(Collectors.toList()) 
    }
    
    public void imprimirFiltradas(Predicate<ContaCorrente> filtro) {
        filtrar(filtro).forEach(c ->
            System.out.printf("No. %d || %-20s || %.2f%n", c.getNumero(), c.getTitular(), c.getSaldo())
        );
    }


    //ORDERNAÇÃO

    public void listarOrdenadoPorSaldo() {
        contas.stream()
              .sorted(POR_SALDO_DESC)
              .forEach(c -> System.out.printf("%d || %-20s || %.2f%n",
                      c.getNumero(), c.getTitular(), c.getSaldo()));
    }

    public void listarOrdenadoPorTitular() {
        contas.stream()
              .sorted(POR_TITULAR)
              .forEach(c -> System.out.printf("%d || %-20s || %.2f%n",
                      c.getNumero(), c.getTitular(), c.getSaldo()));
    }

    public ContaCorrente buscarConta(int numero) {
        return contas.stream()
                    .filter(c -> c.getNumero() == numero)
                    .findFirst()
                    .orElse(null); // retorna null se não encontrar
    }

    public void aplicarTarifaNaConta(ContaCorrente conta, TarifaStrategy strategy) {
        double tarifa = TarifaService.aplicarTarifa(conta, strategy); // altera saldo em memória
        try {
            if (dao.atualizarSaldo(conta.getNumero(), conta.getSaldo())) {
                System.out.printf("Tarifa aplicada: %.2f | Novo saldo: %.2f%n",
                                tarifa, conta.getSaldo());
            } else {
                System.out.println("Erro: não foi possível atualizar o saldo no banco.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar saldo no banco: " + e.getMessage());
        }
    }

    public void recarregar() {
        try {
            contas.clear();              // limpa a memória
            contas.addAll(dao.listar()); // adiciona do banco
        } catch (SQLException e) {
            System.out.println("Erro ao recarregar: " + e.getMessage());
        }
}
}