package model;

import controller.FuncoesUteis;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "orcamento")
public class Orcamento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date dataCriacao = new Date();

    @Column(precision = 12, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusOrcamento status = StatusOrcamento.PENDENTE;

    @Column(length = 2000)
    private String observacoes;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataConsulta;

    @Column(length = 300)
    private String procedimentoConsulta;

    @Column(length = 1000)
    private String observacoesConsulta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemOrcamento> itens = new ArrayList<>();

    @OneToOne(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ConsentimentoTratamento consentimentoTratamento;

    public Orcamento() {
    }

    public Orcamento(Paciente paciente, Usuario usuario, String observacoes) {
        this.paciente = paciente;
        this.usuario = usuario;
        this.observacoes = observacoes;
        this.dataCriacao = new Date();
        this.status = StatusOrcamento.PENDENTE;
    }

    public void adicionarItem(ItemOrcamento item) {
        item.setOrcamento(this);
        item.recalcularTotal();
        itens.add(item);
        recalcularValorTotal();
    }

    public void recalcularValorTotal() {
        valorTotal = BigDecimal.ZERO;
        for (ItemOrcamento item : itens) {
            item.recalcularTotal();
            valorTotal = valorTotal.add(item.getValorTotal());
        }
        valorTotal = valorTotal.setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Date getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Date dataCriacao) { this.dataCriacao = dataCriacao; }
    public String getDataCriacaoFormatada() { return FuncoesUteis.dateToStr(dataCriacao); }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public String getValorTotalFormatado() { return FuncoesUteis.moeda(valorTotal); }
    public StatusOrcamento getStatus() { return status; }
    public void setStatus(StatusOrcamento status) { this.status = status; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Date getDataConsulta() { return dataConsulta; }
    public void setDataConsulta(Date dataConsulta) { this.dataConsulta = dataConsulta; }
    public String getDataConsultaFormatada() { return FuncoesUteis.dateTimeToStr(dataConsulta); }
    public String getProcedimentoConsulta() { return procedimentoConsulta; }
    public void setProcedimentoConsulta(String procedimentoConsulta) { this.procedimentoConsulta = procedimentoConsulta; }
    public String getObservacoesConsulta() { return observacoesConsulta; }
    public void setObservacoesConsulta(String observacoesConsulta) { this.observacoesConsulta = observacoesConsulta; }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public List<ItemOrcamento> getItens() { return itens; }
    public void setItens(List<ItemOrcamento> itens) { this.itens = itens; recalcularValorTotal(); }
    public ConsentimentoTratamento getConsentimentoTratamento() { return consentimentoTratamento; }
    public void setConsentimentoTratamento(ConsentimentoTratamento consentimentoTratamento) {
        this.consentimentoTratamento = consentimentoTratamento;
        if (consentimentoTratamento != null && consentimentoTratamento.getOrcamento() != this) {
            consentimentoTratamento.setOrcamento(this);
        }
    }

    @Override
    public String toString() {
        return "#" + (id == null ? "novo" : id) + " - " + paciente + " - " + getValorTotalFormatado();
    }
}
