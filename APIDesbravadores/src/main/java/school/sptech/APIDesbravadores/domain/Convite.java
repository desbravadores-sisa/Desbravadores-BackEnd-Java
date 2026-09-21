package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Convite")
@Getter
@Setter
@ToString
public class Convite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_convite")
    private Integer id;

    private String email;

    @Column(length = 128, nullable = false, unique = true)
    private String token;

    @Transient
    private String tipoConta;

    @ManyToOne
    @JoinColumn(name = "id_perfil", nullable = false)
    private Perfil perfil;

    @Column(name = "status_convite", length = 20)
    private String statusConvite;

    @Column(name = "data_criacao", insertable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Convert(converter = LocalDateToLocalDateTimeConverter.class)
    @Column(name = "data_expiracao", nullable = false)
    private LocalDate dataExpiracao;

    @ManyToOne
    @JoinColumn(name = "id_clube", nullable = false)
    private Clube clube;

    @ManyToOne
    @JoinColumn(name = "id_unidade")
    private Unidade unidade;
}
