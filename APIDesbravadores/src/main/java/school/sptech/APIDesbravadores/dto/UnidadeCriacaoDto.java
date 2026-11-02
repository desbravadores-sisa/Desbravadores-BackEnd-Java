package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UnidadeCriacaoDto {

    @NotBlank(message = "Informe o nome da unidade.")
    @Size(max = 100, message = "O nome da unidade deve ter no máximo 100 caracteres.")
    private String nome;

    @NotBlank(message = "Selecione uma opção.")
    @Pattern(regexp = "Masculino|Feminino|Misto", message = "Selecione uma opção válida.")
    private String genero;

    @NotNull(message = "Informe a idade mínima necessária.")
    @Min(value = 1, message = "A idade mínima deve ser igual ou maior que 1.")
    @Max(value = 100, message = "A idade mínima deve ser igual ou menor que 100.")
    private Integer idadeMinima;

    @Min(value = 0, message = "A idade máxima deve ser igual ou maior que zero.")
    private Integer idadeMaxima;

    @AssertTrue(message = "A idade máxima deve ser maior que a idade mínima necessária.")
    public boolean isIdadeMaximaValida() {
        return idadeMaxima == null || (idadeMinima != null && idadeMaxima > idadeMinima);
    }
}
