package school.sptech.APIDesbravadores.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificacaoResponseDto {

    private Integer id;
    private String titulo;
    private String mensagem;
    private String tipoReferencia;
    private Integer idReferencia;
    private Boolean lida;
    private LocalDateTime dataCriacao;
}
