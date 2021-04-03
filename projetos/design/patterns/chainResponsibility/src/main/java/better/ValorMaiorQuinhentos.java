package main.java.better;

import main.java.shared.Orcamento;

import java.math.BigDecimal;

public class ValorMaiorQuinhentos extends Desconto{

    public ValorMaiorQuinhentos(Desconto proximo) {
        super(proximo);
    }

    public BigDecimal calcular(Orcamento orcamento) {
        if (orcamento.getValor().compareTo(new BigDecimal("500")) > 0) {
            return orcamento.getValor().multiply(new BigDecimal("0.05"));
        }
        return proximo.calcular(orcamento);
    }
}
