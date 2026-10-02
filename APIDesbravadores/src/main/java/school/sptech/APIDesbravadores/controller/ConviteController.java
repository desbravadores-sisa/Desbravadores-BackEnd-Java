package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.domain.Convite;
import school.sptech.APIDesbravadores.dto.*;
import school.sptech.APIDesbravadores.mapper.ConviteMapper;
import school.sptech.APIDesbravadores.service.ConviteService;

import java.util.List;

@RestController
@RequestMapping("/convites")
public class ConviteController {

    private final ConviteService conviteService;

    public ConviteController(ConviteService conviteService) {
        this.conviteService = conviteService;
    }

    @GetMapping("")
    @PreAuthorize("hasRole('DIRETORIA')")
    public ResponseEntity<List<ConviteResponseDto>> listarUnidades(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado, @RequestParam(required = false) String statusConvite){
        Integer idClube = usuariologado.getIdClube();
        List<ConviteResponseDto> response = conviteService.listarConvites(idClube,statusConvite);
        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('DIRETORIA')")
    public ResponseEntity<ConviteResponseDto> criarConvite(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado, @RequestBody @Valid ConviteCriacaoRequestDto request){
        Integer idClube = usuariologado.getIdClube();
        return ResponseEntity.status(201).body(ConviteMapper.toResponse(conviteService.criarConvite(request,idClube)));
    }

    @Operation(summary = "Excluir um convite",
            description = "Deleta permanentemente um convite do sistema. Ação restrita a usuários com perfil de DIRETORIA. O convite obrigatoriamente deve pertencer ao mesmo clube do usuário solicitante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Convite excluído com sucesso",
                    content = @Content), // Sem corpo de resposta, como combinamos
            @ApiResponse(responseCode = "401", description = "Não autorizado (Token ausente ou inválido)",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso Negado (O usuário não é da DIRETORIA ou está tentando excluir convite de outro clube)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Clube ou Convite não encontrado pelo ID",
                    content = @Content)
    })
    @DeleteMapping()
    @PreAuthorize("hasRole('DIRETORIA')")
    public ResponseEntity<Void> deletarConvite(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado, @RequestParam Integer idConvite){
        conviteService.excluirConvite(usuariologado.getIdClube(),idConvite);
        return ResponseEntity.noContent().build();
    }


}
