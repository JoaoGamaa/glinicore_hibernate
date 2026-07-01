package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "especialidade_odontologica")
public class EspecialidadeOdontologica implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @OneToMany(mappedBy = "especialidade", fetch = FetchType.LAZY)
    private List<Atendimento> atendimentos = new ArrayList<>();

    public EspecialidadeOdontologica() {
    }

    public EspecialidadeOdontologica(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public List<Atendimento> getAtendimentos() { return atendimentos; }
    public void setAtendimentos(List<Atendimento> atendimentos) { this.atendimentos = atendimentos; }

    @Override
    public String toString() {
        return nome;
    }
}
