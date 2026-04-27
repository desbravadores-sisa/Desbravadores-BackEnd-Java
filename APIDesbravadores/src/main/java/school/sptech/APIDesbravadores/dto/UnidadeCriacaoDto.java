package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UnidadeCriacaoDto {

    @NotBlank
    @Size(max = 100)
    private String nome;
}
