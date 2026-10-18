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
    public ResponseEntity<List<ConviteResponseDto>> listarUnidades(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado){
        Integer idClube = usuariologado.getIdClube();
        return ResponseEntity.ok(conviteService.listarConvites(idClube).stream().map((item) -> ConviteMapper.toResponse(item)).toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('DIRETORIA')")
    public ResponseEntity<ConviteResponseDto> criarConvite(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado, @RequestBody @Valid ConviteCriacaoDto request){
        Integer idClube = usuariologado.getIdClube();
        System.out.println("Comecei");
        Convite convite = conviteService.criarConvite(request, idClube);
        ConviteResponseDto response = ConviteMapper.toResponse(convite);
        response.setToken(convite.getToken());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/validar")
    public ResponseEntity<Boolean> validarConvite(Integer idConvite){
        return ResponseEntity.ok(conviteService.validarConvite(idConvite));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DIRETORIA')")
    public ResponseEntity<ConviteResponseDto> atualizarConvite(@RequestBody @Valid ConviteUpdateDto updateDto, @PathVariable Integer id){
        return ResponseEntity.ok(ConviteMapper.toResponse(conviteService.atualizarConvite(updateDto, id)));
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
