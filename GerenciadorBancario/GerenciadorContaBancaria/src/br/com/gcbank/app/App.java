package br.com.gcbank.app;

import java.io.IOException;

import javax.swing.JOptionPane;

import br.com.gcbank.exceptions.SaldoInsuficienteException;
import br.com.gcbank.model.ContaCorrente;
import br.com.gcbank.service.ContaService;

public class App {
    public static void main(String[] args) throws Exception {
        ContaService cs = new ContaService();

        try {
            ContaCorrente conta = cs.lerConta("conta.txt");

            JOptionPane.showMessageDialog(null,
                "Conta carregada:\nNúmero: " + conta.getNumero() +
                "\nTitular: " + conta.getTitular() +
                "\nSaldo: R$ " + conta.getSaldo());

            String valorStr = JOptionPane.showInputDialog("Informe o valor para saque:");
            double valor = Double.parseDouble(valorStr);

            try {
                cs.sacarValor(conta, valor);
                JOptionPane.showMessageDialog(null, "Saque realizado com sucesso!");
            } catch (SaldoInsuficienteException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }

            cs.atualizarConta(conta, "conta_atualizada.txt");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao acessar arquivo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
