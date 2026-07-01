package model;

import controller.FuncoesUteis;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "historico_alteracao")
public class HistoricoAlteracao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAlteracao = new Date();

    @Column(length = 1000)
    private String descricaoAlteracao;

    @Column(length = 100)
    private String tipoRegistroAlterado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prontuario_id", nullable = false)
    private Prontuario prontuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public HistoricoAlteracao() {
    }

    public HistoricoAlteracao(String descricaoAlteracao, String tipoRegistroAlterado) {
        this.dataAlteracao = new Date();
        this.descricaoAlteracao = descricaoAlteracao;
        this.tipoRegistroAlterado = tipoRegistroAlterado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Date getDataAlteracao() { return dataAlteracao; }
    public void setDataAlteracao(Date dataAlteracao) { this.dataAlteracao = dataAlteracao; }
    public String getDataAlteracaoFormatada() { return FuncoesUteis.dateTimeToStr(dataAlteracao); }
    public String getDescricaoAlteracao() { return descricaoAlteracao; }
    public void setDescricaoAlteracao(String descricaoAlteracao) { this.descricaoAlteracao = descricaoAlteracao; }
    public String getTipoRegistroAlterado() { return tipoRegistroAlterado; }
    public void setTipoRegistroAlterado(String tipoRegistroAlterado) { this.tipoRegistroAlterado = tipoRegistroAlterado; }
    public Prontuario getProntuario() { return prontuario; }
    public void setProntuario(Prontuario prontuario) { this.prontuario = prontuario; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
