package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Perfil")
@Getter
@Setter
@ToString
public class Perfil {

    @Id
    @Column(name = "id_perfil")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 45)
    private String nome;

    @Column(length = 100)
    private String descricao;

}
