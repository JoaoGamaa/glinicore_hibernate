package model;

import controller.FuncoesUteis;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "paciente")
public class Paciente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String nome;

    @Column(unique = true, length = 14)
    private String cpf;

    @Temporal(TemporalType.DATE)
    private Date dataNascimento;

    @Column(length = 20)
    private String telefone;

    @Column(length = 160)
    private String email;

    @Lob
    private byte[] fotoIdentificacao;

    @Temporal(TemporalType.DATE)
    private Date dataCadastro = new Date();

    private Boolean ativo = true;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Anamnese> anamneses = new ArrayList<>();

    @OneToOne(mappedBy = "paciente", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Prontuario prontuario;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Orcamento> orcamentos = new ArrayList<>();

    public Paciente() {
    }

    public Paciente(String nome, String cpf, Date dataNascimento, String telefone, String email, byte[] fotoIdentificacao, Endereco endereco) {
        this.nome = nome;
        setCpf(cpf);
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.email = email;
        this.fotoIdentificacao = fotoIdentificacao;
        setEndereco(endereco);
        abrirProntuarioSeNecessario();
    }

    public void abrirProntuarioSeNecessario() {
        if (this.prontuario == null) {
            Prontuario novo = new Prontuario(new Date(), "Prontuário aberto automaticamente no cadastro do paciente.");
            novo.setPaciente(this);
            this.prontuario = novo;
        }
    }

    public void adicionarAnamnese(Anamnese anamnese) {
        anamnese.setPaciente(this);
        anamneses.add(anamnese);
    }

    public void adicionarOrcamento(Orcamento orcamento) {
        orcamento.setPaciente(this);
        orcamentos.add(orcamento);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) {
        String numeros = FuncoesUteis.somenteNumeros(cpf);
        this.cpf = numeros.isBlank() ? null : numeros;
    }
    public Date getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(Date dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getDataNascimentoFormatada() { return FuncoesUteis.dateToStr(dataNascimento); }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public byte[] getFotoIdentificacao() { return fotoIdentificacao; }
    public void setFotoIdentificacao(byte[] fotoIdentificacao) { this.fotoIdentificacao = fotoIdentificacao; }
    public Date getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(Date dataCadastro) { this.dataCadastro = dataCadastro; }
    public String getDataCadastroFormatada() { return FuncoesUteis.dateToStr(dataCadastro); }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
        if (endereco != null) {
            endereco.setPaciente(this);
        }
    }
    public List<Anamnese> getAnamneses() { return anamneses; }
    public void setAnamneses(List<Anamnese> anamneses) { this.anamneses = anamneses; }
    public Prontuario getProntuario() { return prontuario; }
    public void setProntuario(Prontuario prontuario) {
        this.prontuario = prontuario;
        if (prontuario != null) {
            prontuario.setPaciente(this);
        }
    }
    public List<Orcamento> getOrcamentos() { return orcamentos; }
    public void setOrcamentos(List<Orcamento> orcamentos) { this.orcamentos = orcamentos; }

    @Override
    public String toString() {
        return nome == null ? "Paciente" : nome;
    }
}
