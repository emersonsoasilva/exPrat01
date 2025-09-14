package br.com.gcbank.model;

import br.com.gcbank.exceptions.SaldoInsuficienteException;

public abstract class Conta {
    protected int numero;
    protected String titular;
    protected double saldo;

    // Construtor
    
    public Conta(int numero, String titular, double saldo) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = saldo;
    }

    //Métodos

    public abstract void sacar(double valor) throws SaldoInsuficienteException;

    public abstract void depositar(double valor);

    public void imprimirDados() {
        System.out.println("Conta: " + numero);
        System.out.println("Titular: " + titular);
        System.out.println("Saldo atual: R$ " + saldo);
    }

    // Getters and Setters

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    @Override
    public String toString() {
        return "Conta [numero=" + numero + ", titular=" + titular + ", saldo=" + saldo + "]";
    }


    
}
