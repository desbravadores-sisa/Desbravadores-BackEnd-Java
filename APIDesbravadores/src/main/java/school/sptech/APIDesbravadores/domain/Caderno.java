package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    @JoinColumn(name = "id_clube", nullable = false)
    private Clube clube;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(name = "idade_alvo", nullable = false)
    private Integer idadeAlvo;
}