package br.com.gcbank.service;

import br.com.gcbank.exceptions.SaldoInsuficienteException;
import br.com.gcbank.model.Conta;
import br.com.gcbank.model.ContaCorrente;
import br.com.gcbank.banco.contaDAO;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ContaService {

    private ArrayList<ContaCorrente> contas = new ArrayList<>();
    private contaDAO contaDAO;

     public ContaService() {
        this.contaDAO = new contaDAO();     
    }

    public ContaCorrente lerConta(String caminho) throws IOException {
        ArrayList<String> linhas = new ArrayList<>(Files.readAllLines(Paths.get(caminho)));
        if (linhas.isEmpty()) return null;

        String[] dados = linhas.get(0).split(",");
        int numero = Integer.parseInt(dados[0].trim());
        String titular = dados[1].trim();
        double saldo = Double.parseDouble(dados[2].trim());

        ContaCorrente conta = new ContaCorrente(numero, titular, saldo);
        return conta;
    }

    public void transferir(int numeroOrigem, int numeroDestino, double valor) {
        contaDAO.transferir(numeroOrigem, numeroDestino, valor);
    }

    
    public List<ContaCorrente> carregarTodasContas() throws IOException {
        List<Conta> contas = contaDAO.listar();
        return contas.stream().map(c -> (ContaCorrente) c).toList();
    }

    public void adicionarConta(ContaCorrente conta) {
        contas.add(conta);
        contaDAO.inserir(conta); 
    }

    public ContaCorrente buscarConta(int numero) {
       Conta c = contaDAO.buscarPorNumero(numero);
        if (c != null) return (ContaCorrente) c;
        return null;
    }

    public ArrayList<ContaCorrente> getContas() {
        List<Conta> todas = contaDAO.listar();
        ArrayList<ContaCorrente> ccList = new ArrayList<>();
        for (Conta c : todas) {
            ccList.add((ContaCorrente) c);
        }
        return ccList;
    }

    public void sacarValor(ContaCorrente conta, double valor) throws SaldoInsuficienteException {
         conta.sacar(valor);
        contaDAO.atualizarSaldo(conta.getNumero(), conta.getSaldo());
    }

    public void depositarValor(ContaCorrente conta, double valor) {
        conta.depositar(valor);
        contaDAO.atualizarSaldo(conta.getNumero(), conta.getSaldo());
    }

    public void atualizarConta(ContaCorrente conta, String caminho) throws IOException {
        String dados = conta.getNumero() + "," + conta.getTitular() + "," + conta.getSaldo();
        Files.write(Paths.get(caminho), dados.getBytes());
    }
    
    public void salvarContas(String caminho) throws IOException {
        ArrayList<String> linhas = new ArrayList<>();
        for (ContaCorrente c : contas) {
            linhas.add(c.getNumero() + "," + c.getTitular() + "," + c.getSaldo());
        }
        Files.write(Paths.get(caminho), linhas, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public List<Conta> getConta() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    public void removerConta(int numero){
       contaDAO.remover(numero);
    }
}