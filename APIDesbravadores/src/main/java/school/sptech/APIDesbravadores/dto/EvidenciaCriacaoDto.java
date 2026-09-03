package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EvidenciaCriacaoDto {

    @NotNull
    private Integer idTarefaUnidade;

    @Size(max = 500)
    private String urlS3;

    @Size(max = 1000)
    private String comentarioFeedback;
}
