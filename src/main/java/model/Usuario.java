package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(unique = true, length = 160)
    private String email;

    @Column(length = 80)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoUsuario tipoUsuario = TipoUsuario.RECEPCAO;

    private Boolean ativo = true;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Atendimento> atendimentos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<HistoricoAlteracao> historicoAlteracoes = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Orcamento> orcamentos = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(String nome, String email, String senha, TipoUsuario tipoUsuario) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
        this.ativo = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public List<Atendimento> getAtendimentos() { return atendimentos; }
    public void setAtendimentos(List<Atendimento> atendimentos) { this.atendimentos = atendimentos; }
    public List<HistoricoAlteracao> getHistoricoAlteracoes() { return historicoAlteracoes; }
    public void setHistoricoAlteracoes(List<HistoricoAlteracao> historicoAlteracoes) { this.historicoAlteracoes = historicoAlteracoes; }
    public List<Orcamento> getOrcamentos() { return orcamentos; }
    public void setOrcamentos(List<Orcamento> orcamentos) { this.orcamentos = orcamentos; }

    @Override
    public String toString() {
        return nome;
    }
}
