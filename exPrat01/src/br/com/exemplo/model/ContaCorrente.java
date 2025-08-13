package br.com.exemplo.model;

import br.com.exemplo.exception.SaldoInsuficienteException;

public class ContaCorrente extends Conta{

    public ContaCorrente(int numero, String titular, double saldo) {
        super(numero, titular, saldo);
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        System.out.println("Insira o valor do saque: R$ " + valor);

        if (valor <= 0) {
            throw new SaldoInsuficienteException("Valor de saque menor ou igual a 0.");
        }

        if (valor > saldo) {
            throw new SaldoInsuficienteException("Saldo insufiente para saque.");
        }

        saldo -= valor;
    };


}


