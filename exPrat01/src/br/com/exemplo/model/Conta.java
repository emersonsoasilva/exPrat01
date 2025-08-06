package br.com.exemplo.model;
import br.com.exemplo.exception.SaldoInsuficienteException;



public abstract class Conta {

    private  int numero;
    private  String titular;
    private  double saldo;

    Conta(int numero, String titular, double saldo) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = 0;
    }

    abstract void sacar(double valor) throws SaldoInsuficienteException;

    public double depositar(double valor) {
            return valor += saldo;
    }

    public void ImprimirDados() {
        System.out.println("No. da conta: " + numero );
        System.out.println("Nome do titular: " + titular);
        System.out.println("Saldo atual: R$ " + String.format("%.2f", saldo));
    }


}
