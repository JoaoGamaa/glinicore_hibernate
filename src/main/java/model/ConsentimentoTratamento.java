package model;

import controller.FuncoesUteis;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "consentimento_tratamento")
public class ConsentimentoTratamento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date dataAssinatura = new Date();

    @Column(length = 250)
    private String assinaturaDigital;

    @Column(length = 5000)
    private String termo;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orcamento_id", nullable = false, unique = true)
    private Orcamento orcamento;

    public ConsentimentoTratamento() {
    }

    public ConsentimentoTratamento(Date dataAssinatura, String assinaturaDigital, String termo, Orcamento orcamento) {
        this.dataAssinatura = dataAssinatura == null ? new Date() : dataAssinatura;
        this.assinaturaDigital = assinaturaDigital;
        this.termo = termo;
        this.orcamento = orcamento;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Date getDataAssinatura() { return dataAssinatura; }
    public void setDataAssinatura(Date dataAssinatura) { this.dataAssinatura = dataAssinatura; }
    public String getDataAssinaturaFormatada() { return FuncoesUteis.dateToStr(dataAssinatura); }
    public String getAssinaturaDigital() { return assinaturaDigital; }
    public void setAssinaturaDigital(String assinaturaDigital) { this.assinaturaDigital = assinaturaDigital; }
    public String getTermo() { return termo; }
    public void setTermo(String termo) { this.termo = termo; }
    public Orcamento getOrcamento() { return orcamento; }
    public void setOrcamento(Orcamento orcamento) {
        this.orcamento = orcamento;
        if (orcamento != null && orcamento.getConsentimentoTratamento() != this) {
            orcamento.setConsentimentoTratamento(this);
        }
    }
}
