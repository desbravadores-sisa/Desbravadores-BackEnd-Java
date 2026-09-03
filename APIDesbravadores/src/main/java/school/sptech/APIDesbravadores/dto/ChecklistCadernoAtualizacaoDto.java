package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChecklistCadernoAtualizacaoDto {

    @NotNull
    private Integer idExecucaoCaderno;

    @NotNull
    private Integer idDesbravador;

    @NotNull
    private Boolean concluiuTarefa;
}
