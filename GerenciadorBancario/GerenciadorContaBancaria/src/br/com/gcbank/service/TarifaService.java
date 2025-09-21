package br.com.gcbank.service;

import br.com.gcbank.model.ContaCorrente;
import br.com.gcbank.strategy.TarifaStrategy;


public class TarifaService {
    public static double aplicarTarifa(ContaCorrente conta, TarifaStrategy strategy) {
        double tarifa = strategy.calcular(conta.getSaldo());
        conta.setSaldo(conta.getSaldo() - tarifa);
        return tarifa;
    }
}
