package school.sptech.APIDesbravadores.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ExecucaoCadernoResponseDto {

    private Integer id;

    private Integer idUnidade;

    private Integer idTarefa;

    private Integer idCiclo;

    private String statusKanban;

    private LocalDateTime dataConclusao;
}
