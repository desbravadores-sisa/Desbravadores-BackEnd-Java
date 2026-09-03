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
public class CicloCriacaoDto {

    @NotBlank
    @Size(max = 100)
    private String nome;

    @NotNull
    private Integer idClube;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private Boolean ativo;
}
