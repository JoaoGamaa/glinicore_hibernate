package controller.relatorio;

import java.math.BigDecimal;

public class RelatorioGerencialDTO {

    private final String descricao;
    private final Long quantidade;
    private final BigDecimal valorTotal;
    private final String valorTotalFormatado;

    public RelatorioGerencialDTO(String descricao, Long quantidade, BigDecimal valorTotal) {
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
        this.valorTotalFormatado = valorTotal == null ? "" : String.format("R$ %.2f", valorTotal);
    }

    public String getDescricao() { return descricao; }
    public Long getQuantidade() { return quantidade; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public String getValorTotalFormatado() { return valorTotalFormatado; }
}
