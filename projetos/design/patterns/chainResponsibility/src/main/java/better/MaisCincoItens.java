package main.java.better;

import main.java.shared.Orcamento;

import java.math.BigDecimal;

public class MaisCincoItens extends Desconto{

    public MaisCincoItens(Desconto proximo) {
        super(proximo);
    }

    public BigDecimal calcular(Orcamento orcamento) {
        if (orcamento.getQtdItens() > 5) {
            return orcamento.getValor().multiply(new BigDecimal("0.1"));
        }
        return proximo.calcular(orcamento);
    }
}
