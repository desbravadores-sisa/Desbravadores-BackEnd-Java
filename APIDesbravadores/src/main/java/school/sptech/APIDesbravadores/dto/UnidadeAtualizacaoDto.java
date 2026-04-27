package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UnidadeAtualizacaoDto {

    @NotNull
    private Integer idUnidade;

    @NotBlank
    @Size(max = 100)
    private String nome;

    @PositiveOrZero
    private Integer pontuacao;
}
