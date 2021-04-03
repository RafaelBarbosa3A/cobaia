package main.java.better;

import main.java.shared.Orcamento;

import java.math.BigDecimal;

public class Iss implements Imposto{

    public BigDecimal calcular(Orcamento orcamento) {
        return orcamento.getValor().multiply(new BigDecimal("0.05"));
    }
}
