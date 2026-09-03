package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class DesbravadorCriacaoDto {

    @NotBlank
    @Size(max = 100)
    private String nome;

    @NotNull
    private Integer idClube;

    @NotNull
    private Integer idUnidade;

    private LocalDate dataNascimento;

    @Size(max = 20)
    private String genero;

    private Boolean ativo;
}
