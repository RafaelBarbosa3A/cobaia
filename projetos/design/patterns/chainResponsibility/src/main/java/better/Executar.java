package main.java.better;

import main.java.bad.CalculadoraDesconto;
import main.java.shared.Orcamento;

import java.math.BigDecimal;

public class Executar {
    public static void main(String[] args) {
        Orcamento primeiroOrc = new Orcamento(new BigDecimal("200"), 6);
        Orcamento segundoOrc = new Orcamento(new BigDecimal("1000"), 1);

        CalculadoraDesconto calculadoraDesconto = new CalculadoraDesconto();
        System.out.println(String.format("qtd > 5 R$%.2f", calculadoraDesconto.calcular(primeiroOrc)));
        System.out.println(String.format("valor > 500 R$%.2f", calculadoraDesconto.calcular(segundoOrc)));
    }
}
