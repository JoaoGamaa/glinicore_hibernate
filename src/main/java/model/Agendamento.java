package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "agendamentos")
public class Agendamento implements Serializable {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(length = 160)
    private String procedimento;

    @Column(length = 120)
    private String profissional;

    @Column(length = 40)
    private String status = "Agendado";

    @Column(length = 1000)
    private String observacoes;

    public Agendamento() {
    }

    public Agendamento(Paciente paciente, LocalDate data, LocalTime hora, String procedimento,
            String profissional, String status, String observacoes) {
        this.paciente = paciente;
        this.data = data;
        this.hora = hora;
        this.procedimento = procedimento;
        this.profissional = profissional;
        this.status = status;
        this.observacoes = observacoes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public String getProcedimento() { return procedimento; }
    public void setProcedimento(String procedimento) { this.procedimento = procedimento; }
    public String getProfissional() { return profissional; }
    public void setProfissional(String profissional) { this.profissional = profissional; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getDataFormatada() {
        return data == null ? "" : data.format(FORMATO_DATA);
    }

    public String getHoraFormatada() {
        return hora == null ? "" : hora.format(FORMATO_HORA);
    }
}
