package controller.relatorio;

public class AnamneseRelatorioDTO {

    private final Long id;
    private final String nomePaciente;
    private final String data;
    private final String alergias;
    private final String doencas;
    private final String medicamentos;
    private final String observacoes;

    public AnamneseRelatorioDTO(Long id, String nomePaciente, String data, String alergias,
            String doencas, String medicamentos, String observacoes) {
        this.id = id;
        this.nomePaciente = nomePaciente;
        this.data = data;
        this.alergias = alergias;
        this.doencas = doencas;
        this.medicamentos = medicamentos;
        this.observacoes = observacoes;
    }

    public Long getId() { return id; }
    public String getNomePaciente() { return nomePaciente; }
    public String getData() { return data; }
    public String getAlergias() { return alergias; }
    public String getDoencas() { return doencas; }
    public String getMedicamentos() { return medicamentos; }
    public String getObservacoes() { return observacoes; }
}
