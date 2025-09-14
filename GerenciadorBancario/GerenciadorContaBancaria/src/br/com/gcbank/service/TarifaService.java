package br.com.gcbank.service;

import br.com.gcbank.model.Conta;
import br.com.gcbank.strategy.TarifaStrategy;


public class TarifaService {
    public double calcularTarifa(Conta conta, TarifaStrategy strategy) {
        return strategy.calcular(conta.getSaldo());
    }
}
