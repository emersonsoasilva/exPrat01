package br.com.exemplo.model;
import br.com.exemplo.exception.SaldoInsuficienteException;



public abstract class Conta {

    protected  int numero;
    protected  String titular;
    protected  double saldo;

    Conta(int numero, String titular, double saldo) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = 0;
    }

    abstract void sacar(double valor) throws SaldoInsuficienteException;

    public void depositar(double valor) {
        if (valor > 0) {
            valor += saldo;
        }       
    }

    public void ImprimirDados() {
        System.out.println("No. da conta: " + numero );
        System.out.println("Nome do titular: " + titular);
        System.out.println("Saldo atual: R$ " + String.format("%.2f", saldo));
    }

    public int getNumero() { 
        return numero;
    }
    public String getTitular() {
        return titular;
    }
    public double getSaldo() { 
        return saldo;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

}
