package br.com.gcbank.model;

import br.com.gcbank.exceptions.SaldoInsuficienteException;



public class ContaCorrente extends Conta{

    public ContaCorrente(int numero, String titular, double saldo) {
        super(numero, titular, saldo);
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        if (valor < saldo) {
            saldo -= valor;
        
        }

        else if (valor == 0) {
            System.out.println("Erro: Valor para saque deve ser maior que 0!");
        }

        else if (valor > saldo) {
            throw new SaldoInsuficienteException("Erro: Saldo insuficiente para saque!");
        }
    }


    @Override
    public void depositar(double valor) {
        if (valor == 0) {
            System.out.println("Erro: Valor para depósito deve ser maior que zero!");

        }

        else {
            saldo += valor;
        }
    }
    
}
