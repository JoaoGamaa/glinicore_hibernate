package controller.relatorio;

public class ProntuarioRelatorioDTO {

    private final String tipo;
    private final String data;
    private final String descricao;

    public ProntuarioRelatorioDTO(String tipo, String data, String descricao) {
        this.tipo = tipo;
        this.data = data;
        this.descricao = descricao;
    }

    public String getTipo() { return tipo; }
    public String getData() { return data; }
    public String getDescricao() { return descricao; }
}
