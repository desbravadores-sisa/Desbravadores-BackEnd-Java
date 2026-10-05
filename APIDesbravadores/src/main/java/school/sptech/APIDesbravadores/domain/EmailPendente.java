package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "Email_Pendente")
@Getter
@Setter
public class EmailPendente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_email") private Integer id;
    @Column(nullable = false) private String destinatario;
    @Column(nullable = false) private String assunto;
    @Column(nullable = false, columnDefinition = "TEXT") private String conteudo;
    @Column(nullable = false) private boolean enviado;
    @Column(nullable = false) private int tentativas;
    @Column(name = "proxima_tentativa", nullable = false) private LocalDateTime proximaTentativa = LocalDateTime.now();
}
