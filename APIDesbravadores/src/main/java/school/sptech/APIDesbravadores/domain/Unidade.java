package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Unidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidade")
    private Integer id;

    private String nome;

    private Integer pontuacao;

    @ManyToOne
    @JoinColumn(name = "id_clube")
    private Clube clube;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(Integer pontuacao) {
        this.pontuacao = pontuacao;
    }

    public Clube getClube() {
        return clube;
    }

    public void setClube(Clube clube) {
        this.clube = clube;
    }

    @Override
    public String toString() {
        return "Unidade{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", pontuacao=" + pontuacao +
                ", clube=" + clube +
                '}';
    }
}
