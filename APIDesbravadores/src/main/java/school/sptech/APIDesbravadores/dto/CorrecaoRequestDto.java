package school.sptech.APIDesbravadores.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record CorrecaoRequestDto(@NotBlank @Size(max = 2000) String justificativa) {}
