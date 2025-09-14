package br.com.gcbank.exceptions;

import java.lang.Exception;

public class SaldoInsuficienteException extends Exception{

    public SaldoInsuficienteException(String message) {
        super(message);
    }

}
