package school.sptech.APIDesbravadores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioResponseDto {

    @Schema(description = "ID do usuário", example = "1")
    private Integer id;

    @Schema(description = "Nome do usuário", example = "Maria Silva")
    private String nome;

    @Schema(description = "E-mail do usuário", example = "maria@email.com")
    private String email;

    @Schema(description = "Tipo de conta", example = "DIRETOR")
    private String tipoConta;

    @Schema(description = "ID do clube vinculado", example = "1")
    private Integer idClube;

    @Schema(description = "ID da unidade vinculada", example = "2")
    private Integer idUnidade;
}
