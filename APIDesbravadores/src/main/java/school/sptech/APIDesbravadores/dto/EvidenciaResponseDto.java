package school.sptech.APIDesbravadores.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class EvidenciaResponseDto {

    private Integer id;

    private Integer idTarefaUnidade;

    private String urlS3;

    private String comentarioFeedback;

    private LocalDateTime dataEnvio;
}
