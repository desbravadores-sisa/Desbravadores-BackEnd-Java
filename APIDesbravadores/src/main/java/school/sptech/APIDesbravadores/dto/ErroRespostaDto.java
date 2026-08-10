package school.sptech.APIDesbravadores.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Corpo padrão de resposta para erros da API")
public record ErroRespostaDto(

        @Schema(description = "Momento em que o erro ocorreu")
        LocalDateTime timestamp,

        @Schema(description = "Código HTTP", example = "400")
        int status,

        @Schema(description = "Nome do código HTTP", example = "Bad Request")
        String error,

        @Schema(description = "Descrição do que deu errado", example = "Status inválido: Concluido!")
        String message,

        @Schema(description = "Rota que originou o erro", example = "/tarefas/1/status")
        String path,

        @Schema(description = "Erros por campo, presente apenas em falhas de validação")
        Map<String, String> campos
) {
}
