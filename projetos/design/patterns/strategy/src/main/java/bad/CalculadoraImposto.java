package main.java.bad;

import main.java.shared.Orcamento;

import java.math.BigDecimal;

public class CalculadoraImposto {

    public BigDecimal calcular(Orcamento orcamento, TipoImposto imposto) {
        BigDecimal impostoAplicado = switch (imposto) {
            case ICMS -> orcamento.getValor().multiply(new BigDecimal("0.1"));
            case ISS -> orcamento.getValor().multiply(new BigDecimal("0.05"));
            default -> BigDecimal.ZERO;
        };
        return impostoAplicado;
    }
}
