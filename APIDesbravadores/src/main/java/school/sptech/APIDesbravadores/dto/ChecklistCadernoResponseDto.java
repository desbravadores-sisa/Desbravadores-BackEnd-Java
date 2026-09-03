package school.sptech.APIDesbravadores.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ChecklistCadernoResponseDto {

    private Integer id;

    private Integer idExecucaoCaderno;

    private Integer idDesbravador;

    private Boolean concluiuTarefa;

    private LocalDateTime dataMarcacao;
}
