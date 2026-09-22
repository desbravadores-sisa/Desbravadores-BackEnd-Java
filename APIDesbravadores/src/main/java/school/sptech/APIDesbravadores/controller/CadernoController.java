package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.APIDesbravadores.dto.GrupoCadernoDto;
import school.sptech.APIDesbravadores.dto.UsuarioDetalhesDto;
import school.sptech.APIDesbravadores.service.CadernoService;

import java.util.List;

@RestController
@RequestMapping("/cadernos")
@Tag(name = "Cadernos", description = "Endpoints para visualização e gestão dos cadernos de classes")
public class CadernoController {

    private final CadernoService cadernoService;

    public CadernoController(CadernoService cadernoService) {
        this.cadernoService = cadernoService;
    }

    @PreAuthorize("hasRole('DIRETORIA')")
    @GetMapping("/resumo")
    @Operation(
            summary = "Obter resumo dos cadernos",
            description = "Retorna a lista de cadernos agrupados por faixa etária, contendo o total de requisitos e desbravadores vinculados. Exclusivo para usuários com perfil de DIRETORIA.",
            security = @SecurityRequirement(name = "bearer-jwt") // Habilita o cadeado do JWT no Swagger UI
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resumo dos cadernos recuperado com sucesso"),
            @ApiResponse(responseCode = "204", description = "Não há cadernos cadastrados para o clube solicitado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado. Token JWT ausente, inválido ou expirado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado. O usuário não possui credenciais de Diretoria", content = @Content),
            @ApiResponse(responseCode = "404", description = "O clube vinculado ao usuário não foi encontrado na base de dados", content = @Content)
    })
    public ResponseEntity<List<GrupoCadernoDto>> obterResumosCadernos(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado){
        List<GrupoCadernoDto> resumo = cadernoService.listarResumosCadernos(usuariologado.getIdClube());
        return resumo.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(resumo);
    }
}
