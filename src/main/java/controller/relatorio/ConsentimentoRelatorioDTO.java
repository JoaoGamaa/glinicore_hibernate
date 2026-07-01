package controller.relatorio;

public class ConsentimentoRelatorioDTO {

    private final Long id;
    private final String nomePaciente;
    private final Long orcamentoId;
    private final String data;
    private final String termo;
    private final String assinaturaDigital;

    public ConsentimentoRelatorioDTO(Long id, String nomePaciente, Long orcamentoId,
            String data, String termo, String assinaturaDigital) {
        this.id = id;
        this.nomePaciente = nomePaciente;
        this.orcamentoId = orcamentoId;
        this.data = data;
        this.termo = termo;
        this.assinaturaDigital = assinaturaDigital;
    }

    public Long getId() { return id; }
    public String getNomePaciente() { return nomePaciente; }
    public Long getOrcamentoId() { return orcamentoId; }
    public String getData() { return data; }
    public String getTermo() { return termo; }
    public String getAssinaturaDigital() { return assinaturaDigital; }
}
