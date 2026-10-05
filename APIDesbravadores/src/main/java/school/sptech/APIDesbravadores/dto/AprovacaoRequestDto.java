package school.sptech.APIDesbravadores.dto;
import jakarta.validation.constraints.PositiveOrZero;
public record AprovacaoRequestDto(@PositiveOrZero Integer pontuacao) {}
