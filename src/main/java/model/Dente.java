package model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "dente")
public class Dente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numero;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CondicaoDente condicao = CondicaoDente.NORMAL;

    @Column(length = 500)
    private String observacao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "odontograma_id", nullable = false)
    private Odontograma odontograma;

    public Dente() {
    }

    public Dente(Integer numero, CondicaoDente condicao, String observacao) {
        validarNumero(numero);
        this.numero = numero;
        this.condicao = condicao == null ? CondicaoDente.NORMAL : condicao;
        this.observacao = observacao;
    }

    public static void validarNumero(Integer numero) {
        if (numero == null) {
            throw new IllegalArgumentException("Número do dente é obrigatório.");
        }
        int dezena = numero / 10;
        int unidade = numero % 10;
        if ((dezena < 1 || dezena > 4) || (unidade < 1 || unidade > 8)) {
            throw new IllegalArgumentException("Número de dente inválido pela notação FDI: " + numero);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { validarNumero(numero); this.numero = numero; }
    public CondicaoDente getCondicao() { return condicao; }
    public void setCondicao(CondicaoDente condicao) { this.condicao = condicao; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public Odontograma getOdontograma() { return odontograma; }
    public void setOdontograma(Odontograma odontograma) { this.odontograma = odontograma; }
}
