package controller.relatorio;

public class OrcamentoRelatorioDTO {

    private final Long id;
    private final String nomePaciente;
    private final String data;
    private final String status;
    private final String valorTotal;
    private final String observacoes;
    private final String itens;

    public OrcamentoRelatorioDTO(Long id, String nomePaciente, String data, String status,
            String valorTotal, String observacoes, String itens) {
        this.id = id;
        this.nomePaciente = nomePaciente;
        this.data = data;
        this.status = status;
        this.valorTotal = valorTotal;
        this.observacoes = observacoes;
        this.itens = itens;
    }

    public Long getId() { return id; }
    public String getNomePaciente() { return nomePaciente; }
    public String getData() { return data; }
    public String getStatus() { return status; }
    public String getValorTotal() { return valorTotal; }
    public String getObservacoes() { return observacoes; }
    public String getItens() { return itens; }
}
