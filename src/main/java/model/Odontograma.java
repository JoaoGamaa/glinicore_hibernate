package model;

import controller.FuncoesUteis;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "odontograma")
public class Odontograma implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date dataRegistro = new Date();

    @Column(length = 2000)
    private String observacoes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prontuario_id", nullable = false)
    private Prontuario prontuario;

    @OneToMany(mappedBy = "odontograma", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Dente> dentes = new ArrayList<>();

    public Odontograma() {
    }

    public Odontograma(String observacoes) {
        this.dataRegistro = new Date();
        this.observacoes = observacoes;
    }

    public void adicionarDente(Dente dente) {
        dente.setOdontograma(this);
        dentes.add(dente);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Date getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(Date dataRegistro) { this.dataRegistro = dataRegistro; }
    public String getDataRegistroFormatada() { return FuncoesUteis.dateToStr(dataRegistro); }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Prontuario getProntuario() { return prontuario; }
    public void setProntuario(Prontuario prontuario) { this.prontuario = prontuario; }
    public List<Dente> getDentes() { return dentes; }
    public void setDentes(List<Dente> dentes) { this.dentes = dentes; }
}
