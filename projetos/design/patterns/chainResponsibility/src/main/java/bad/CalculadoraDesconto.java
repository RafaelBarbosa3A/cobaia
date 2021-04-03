package main.java.bad;

import main.java.better.Desconto;
import main.java.better.MaisCincoItens;
import main.java.better.SemDesconto;
import main.java.better.ValorMaiorQuinhentos;
import main.java.shared.Orcamento;

import java.math.BigDecimal;

public class CalculadoraDesconto {

    public BigDecimal calcular(Orcamento orcamento) {
        Desconto desconto = new MaisCincoItens(new ValorMaiorQuinhentos(new SemDesconto()));
        return desconto.calcular(orcamento);
    }
}
