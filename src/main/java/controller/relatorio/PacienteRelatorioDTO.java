package controller.relatorio;

public class PacienteRelatorioDTO {

    private final Long id;
    private final String nome;
    private final String cpf;
    private final String dataNascimento;
    private final String telefone;
    private final String email;
    private final String cidade;
    private final String bairro;
    private final String status;

    public PacienteRelatorioDTO(Long id, String nome, String cpf, String dataNascimento,
            String telefone, String email, String cidade, String bairro, String status) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.email = email;
        this.cidade = cidade;
        this.bairro = bairro;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getDataNascimento() { return dataNascimento; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public String getCidade() { return cidade; }
    public String getBairro() { return bairro; }
    public String getStatus() { return status; }
}
