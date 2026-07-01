package controller.relatorio;

public class OdontogramaRelatorioDTO {

    private final Long id;
    private final String nomePaciente;
    private final String data;
    private final Integer dente;
    private final String condicao;
    private final String observacoes;

    public OdontogramaRelatorioDTO(Long id, String nomePaciente, String data, Integer dente,
            String condicao, String observacoes) {
        this.id = id;
        this.nomePaciente = nomePaciente;
        this.data = data;
        this.dente = dente;
        this.condicao = condicao;
        this.observacoes = observacoes;
    }

    public Long getId() { return id; }
    public String getNomePaciente() { return nomePaciente; }
    public String getData() { return data; }
    public Integer getDente() { return dente; }
    public String getCondicao() { return condicao; }
    public String getObservacoes() { return observacoes; }
}
