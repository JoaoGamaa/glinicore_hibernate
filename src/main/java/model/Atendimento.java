package model;

import controller.FuncoesUteis;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "atendimento")
public class Atendimento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAtendimento = new Date();

    @Column(length = 1000)
    private String descricao;

    @Column(length = 1000)
    private String procedimentoRealizado;

    @Column(length = 1000)
    private String observacoes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prontuario_id", nullable = false)
    private Prontuario prontuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "especialidade_id")
    private EspecialidadeOdontologica especialidade;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Atendimento() {
    }

    public Atendimento(Date dataAtendimento, String descricao, String procedimentoRealizado, String observacoes) {
        this.dataAtendimento = dataAtendimento == null ? new Date() : dataAtendimento;
        this.descricao = descricao;
        this.procedimentoRealizado = procedimentoRealizado;
        this.observacoes = observacoes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Date getDataAtendimento() { return dataAtendimento; }
    public void setDataAtendimento(Date dataAtendimento) { this.dataAtendimento = dataAtendimento; }
    public String getDataAtendimentoFormatada() { return FuncoesUteis.dateTimeToStr(dataAtendimento); }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getProcedimentoRealizado() { return procedimentoRealizado; }
    public void setProcedimentoRealizado(String procedimentoRealizado) { this.procedimentoRealizado = procedimentoRealizado; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Prontuario getProntuario() { return prontuario; }
    public void setProntuario(Prontuario prontuario) { this.prontuario = prontuario; }
    public EspecialidadeOdontologica getEspecialidade() { return especialidade; }
    public void setEspecialidade(EspecialidadeOdontologica especialidade) { this.especialidade = especialidade; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
