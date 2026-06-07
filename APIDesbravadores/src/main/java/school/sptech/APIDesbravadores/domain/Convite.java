package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class Convite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_convite")
    private Integer id;

    private String email;

    @Column(columnDefinition = "CHAR(64)")
    private String token;

    private String tipoConta;

    private LocalDate dataExpiracao;

    private String statusConvite;

    @ManyToOne
    @JoinColumn(name = "id_clube")
    private Clube clube;

    @ManyToOne
    @JoinColumn(name = "id_unidade")
    private Unidade unidade;
}
