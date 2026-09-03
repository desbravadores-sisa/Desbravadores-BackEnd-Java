package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ExecucaoCadernoCriacaoDto {

    @NotNull
    private Integer idUnidade;

    @NotNull
    private Integer idTarefa;

    @NotNull
    private Integer idCiclo;

    @NotBlank
    private String statusKanban;
}
