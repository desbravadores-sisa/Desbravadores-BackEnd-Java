package school.sptech.APIDesbravadores.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.*;
import java.util.List;
@Getter @Setter
public class TarefaUpdateDto {
    @NotBlank @Size(max = 150) private String nome;
    private String descricao;
    @PositiveOrZero private Integer pontuacao;
    private LocalDateTime prazoEntrega;
    private LocalDate dataInicio;
    private String instrucoesEvidencia;
    private List<@NotNull Integer> unidadeIds;
    private Boolean todasUnidades;
}
