package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.persistence.*;

@Entity
@Table(name = "item_orcamento")
public class ItemOrcamento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String descricaoProcedimento;

    private Integer quantidade = 1;

    @Column(precision = 12, scale = 2)
    private BigDecimal valorUnitario = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orcamento_id", nullable = false)
    private Orcamento orcamento;

    public ItemOrcamento() {
    }

    public ItemOrcamento(String descricaoProcedimento, Integer quantidade, BigDecimal valorUnitario) {
        this.descricaoProcedimento = descricaoProcedimento;
        this.quantidade = quantidade == null ? 1 : quantidade;
        this.valorUnitario = valorUnitario == null ? BigDecimal.ZERO : valorUnitario;
        recalcularTotal();
    }

    public void recalcularTotal() {
        if (quantidade == null || quantidade < 1) quantidade = 1;
        if (valorUnitario == null) valorUnitario = BigDecimal.ZERO;
        valorTotal = valorUnitario.multiply(BigDecimal.valueOf(quantidade)).setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescricaoProcedimento() { return descricaoProcedimento; }
    public void setDescricaoProcedimento(String descricaoProcedimento) { this.descricaoProcedimento = descricaoProcedimento; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; recalcularTotal(); }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; recalcularTotal(); }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public Orcamento getOrcamento() { return orcamento; }
    public void setOrcamento(Orcamento orcamento) { this.orcamento = orcamento; }
}
