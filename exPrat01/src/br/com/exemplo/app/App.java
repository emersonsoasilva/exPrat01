package br.com.exemplo.app;

import br.com.exemplo.exception.SaldoInsuficienteException;
import br.com.exemplo.model.*;
import br.com.exemplo.service.ContaService;
import java.io.IOException;
import javax.swing.*;



public class App {
    public static void main(String[] args) {
        // TODO code application logic here
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
