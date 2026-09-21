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

import java.time.LocalDateTime;

@Entity
@Table(name = "Notificacao")
@Getter
@Setter
@ToString
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacao")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_clube", nullable = false)
    private Clube clube;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false)
    private String mensagem;

    @Column(name = "tipo_referencia", length = 45)
    private String tipoReferencia;

    @Column(name = "id_referencia")
    private Integer idReferencia;

    @Column(nullable = false)
    private Boolean lida = false;

    @Column(name = "data_criacao", insertable = false, updatable = false)
    private LocalDateTime dataCriacao;
}