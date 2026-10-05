package school.sptech.APIDesbravadores.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.*;
import java.util.List;
@Getter @Setter
public class TarefaCreateDto {
    @Pattern(regexp = "[0-9a-fA-F-]{36}") private String requestId;
    private Integer fkClube;
    private Integer fkUnidade;
    private List<@NotNull Integer> unidadeIds;
    private Boolean todasUnidades = false;
    private Integer idCiclo;
    private Integer idCaderno;
    private String tipoTarefa = "GERAL";
    @NotBlank @Size(max = 150) private String nome;
    private String descricao;
    private String instrucoesEvidencia;
    @PositiveOrZero private Integer pontuacao;
    private LocalDate dataInicio;
    private LocalDateTime prazoEntrega;
}
