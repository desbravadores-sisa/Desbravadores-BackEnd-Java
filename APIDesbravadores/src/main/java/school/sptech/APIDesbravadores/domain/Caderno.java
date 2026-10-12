package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Caderno")
@Getter
@Setter
@ToString
public class Caderno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caderno")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_clube")
    private Clube clube;

    @ManyToOne
    @JoinColumn(name = "id_ciclo")
    private Ciclo ciclo;

    private String nome;

    @Column(name = "idade_alvo")
    private Integer idadeAlvo;
}
