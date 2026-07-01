package model;

import controller.FuncoesUteis;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "prontuario")
public class Prontuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date dataAbertura = new Date();

    @Column(length = 2000)
    private String observacoesGerais;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id", nullable = false, unique = true)
    private Paciente paciente;

    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Odontograma> odontogramas = new ArrayList<>();

    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Atendimento> atendimentos = new ArrayList<>();

    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricoAlteracao> historicoAlteracoes = new ArrayList<>();

    public Prontuario() {
    }

    public Prontuario(Date dataAbertura, String observacoesGerais) {
        this.dataAbertura = dataAbertura;
        this.observacoesGerais = observacoesGerais;
    }

    public void adicionarOdontograma(Odontograma odontograma) {
        odontograma.setProntuario(this);
        odontogramas.add(odontograma);
    }

    public void adicionarAtendimento(Atendimento atendimento) {
        atendimento.setProntuario(this);
        atendimentos.add(atendimento);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Date getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(Date dataAbertura) { this.dataAbertura = dataAbertura; }
    public String getDataAberturaFormatada() { return FuncoesUteis.dateToStr(dataAbertura); }
    public String getObservacoesGerais() { return observacoesGerais; }
    public void setObservacoesGerais(String observacoesGerais) { this.observacoesGerais = observacoesGerais; }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    public List<Odontograma> getOdontogramas() { return odontogramas; }
    public void setOdontogramas(List<Odontograma> odontogramas) { this.odontogramas = odontogramas; }
    public List<Atendimento> getAtendimentos() { return atendimentos; }
    public void setAtendimentos(List<Atendimento> atendimentos) { this.atendimentos = atendimentos; }
    public List<HistoricoAlteracao> getHistoricoAlteracoes() { return historicoAlteracoes; }
    public void setHistoricoAlteracoes(List<HistoricoAlteracao> historicoAlteracoes) { this.historicoAlteracoes = historicoAlteracoes; }

    @Override
    public String toString() {
        return "Prontuário de " + (paciente == null ? "paciente" : paciente.getNome());
    }
}
