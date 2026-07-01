package model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "endereco")
public class Endereco implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 120)
    private String rua;

    @Column(length = 20)
    private String numero;

    @Column(length = 80)
    private String bairro;

    @Column(length = 80)
    private String cidade;

    @Column(length = 2)
    private String estado;

    @Column(length = 12)
    private String cep;

    @Column(length = 160)
    private String complemento;

    @OneToOne(mappedBy = "endereco", fetch = FetchType.LAZY)
    private Paciente paciente;

    public Endereco() {
    }

    public Endereco(String rua, String numero, String bairro, String cidade, String estado, String cep, String complemento) {
        this.rua = rua;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
        this.complemento = complemento;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public String getEnderecoCompleto() {
        StringBuilder sb = new StringBuilder();
        if (rua != null && !rua.isBlank()) sb.append(rua);
        if (numero != null && !numero.isBlank()) sb.append(", ").append(numero);
        if (bairro != null && !bairro.isBlank()) sb.append(" - ").append(bairro);
        if (cidade != null && !cidade.isBlank()) sb.append(" - ").append(cidade);
        if (estado != null && !estado.isBlank()) sb.append("/").append(estado);
        return sb.toString();
    }
}
