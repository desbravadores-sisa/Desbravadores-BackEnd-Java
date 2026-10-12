package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PenalidadeCriacaoDto(
        @NotNull @Min(1) @Max(10000) Integer pontos,
        @NotBlank @Size(max = 255) String motivo
) {}
