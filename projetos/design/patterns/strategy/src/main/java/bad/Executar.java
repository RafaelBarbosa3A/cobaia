package main.java.bad;

import main.java.shared.Orcamento;

import java.math.BigDecimal;

public class Executar {
    public static void main(String[] args) {
        Orcamento orcamento = new Orcamento(new BigDecimal("100"));
        CalculadoraImposto calculadoraImposto = new CalculadoraImposto();
        System.out.println(String.format("ICMS aplicado R$%.2f", calculadoraImposto.calcular(orcamento, TipoImposto.ICMS)));
        System.out.println(String.format("ISS aplicado R$%.2f", calculadoraImposto.calcular(orcamento, TipoImposto.ISS)));
    }
}
