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
public class CicloAtualizacaoDto {

    @NotBlank
    @Size(max = 100)
    private String nome;

    /** Ignorado quando há usuário logado: o clube vem do token. */
    private Integer idClube;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private Boolean ativo;
}
