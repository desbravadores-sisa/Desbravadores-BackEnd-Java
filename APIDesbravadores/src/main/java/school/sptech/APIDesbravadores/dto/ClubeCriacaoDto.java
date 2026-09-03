package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ClubeCriacaoDto {

    @NotBlank
    @Size(max = 150)
    private String nome;

    @Size(max = 50)
    private String regiao;

    @Size(max = 100)
    private String cidade;
}
