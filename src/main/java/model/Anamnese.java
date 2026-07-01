package model;

import controller.FuncoesUteis;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "anamnese")
public class Anamnese implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean possuiAlergia = false;
    @Column(length = 500)
    private String alergias;
    private Boolean usaMedicamento = false;
    @Column(length = 500)
    private String medicamentos;
    private Boolean possuiDoenca = false;
    @Column(length = 500)
    private String doencas;
    @Column(length = 2000)
    private String observacoes;
    @Column(length = 2000)
    private String historiaDoencaAtual;
    @Column(length = 2000)
    private String historiaPatologicaPregressa;
    @Column(length = 2000)
    private String historiaFisiologica;
    @Column(length = 2000)
    private String historiaFamiliar;
    @Temporal(TemporalType.DATE)
    private Date dataRegistro = new Date();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    public Anamnese() {
    }

    public Anamnese(Boolean possuiAlergia, String alergias, Boolean usaMedicamento, String medicamentos, Boolean possuiDoenca, String doencas, String observacoes) {
        this.possuiAlergia = possuiAlergia;
        this.alergias = alergias;
        this.usaMedicamento = usaMedicamento;
        this.medicamentos = medicamentos;
        this.possuiDoenca = possuiDoenca;
        this.doencas = doencas;
        this.observacoes = observacoes;
        this.dataRegistro = new Date();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Boolean getPossuiAlergia() { return possuiAlergia; }
    public void setPossuiAlergia(Boolean possuiAlergia) { this.possuiAlergia = possuiAlergia; }
    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }
    public Boolean getUsaMedicamento() { return usaMedicamento; }
    public void setUsaMedicamento(Boolean usaMedicamento) { this.usaMedicamento = usaMedicamento; }
    public String getMedicamentos() { return medicamentos; }
    public void setMedicamentos(String medicamentos) { this.medicamentos = medicamentos; }
    public Boolean getPossuiDoenca() { return possuiDoenca; }
    public void setPossuiDoenca(Boolean possuiDoenca) { this.possuiDoenca = possuiDoenca; }
    public String getDoencas() { return doencas; }
    public void setDoencas(String doencas) { this.doencas = doencas; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public String getHistoriaDoencaAtual() { return historiaDoencaAtual; }
    public void setHistoriaDoencaAtual(String historiaDoencaAtual) { this.historiaDoencaAtual = historiaDoencaAtual; }
    public String getHistoriaPatologicaPregressa() { return historiaPatologicaPregressa; }
    public void setHistoriaPatologicaPregressa(String historiaPatologicaPregressa) { this.historiaPatologicaPregressa = historiaPatologicaPregressa; }
    public String getHistoriaFisiologica() { return historiaFisiologica; }
    public void setHistoriaFisiologica(String historiaFisiologica) { this.historiaFisiologica = historiaFisiologica; }
    public String getHistoriaFamiliar() { return historiaFamiliar; }
    public void setHistoriaFamiliar(String historiaFamiliar) { this.historiaFamiliar = historiaFamiliar; }
    public Date getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(Date dataRegistro) { this.dataRegistro = dataRegistro; }
    public String getDataRegistroFormatada() { return FuncoesUteis.dateToStr(dataRegistro); }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
}
