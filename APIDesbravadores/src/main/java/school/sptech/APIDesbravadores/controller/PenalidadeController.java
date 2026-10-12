package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.dto.PenalidadeCriacaoDto;
import school.sptech.APIDesbravadores.dto.PenalidadeDto;
import school.sptech.APIDesbravadores.dto.UsuarioDetalhesDto;
import school.sptech.APIDesbravadores.service.PenalidadeService;

import java.util.List;

@RestController
@Tag(name = "Penalidades", description = "Descontos de pontos lançados pela Diretoria")
public class PenalidadeController {

    private final PenalidadeService penalidadeService;

    public PenalidadeController(PenalidadeService penalidadeService) {
        this.penalidadeService = penalidadeService;
    }

    @GetMapping("/unidades/{idUnidade}/penalidades")
    @PreAuthorize("hasRole('DIRETORIA')")
    @Operation(summary = "Descontos da unidade no ciclo ativo")
    public ResponseEntity<List<PenalidadeDto>> listar(@PathVariable Integer idUnidade,
                                                      @AuthenticationPrincipal UsuarioDetalhesDto usuario) {
        if (usuario == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(penalidadeService.listarDoCicloAtivo(idUnidade, usuario.getIdClube()));
    }

    @PostMapping("/unidades/{idUnidade}/penalidades")
    @PreAuthorize("hasRole('DIRETORIA')")
    @Operation(summary = "Lançar desconto de pontos", description = "Desconta pontos da unidade no ciclo ativo, com motivo; notifica o conselheiro")
    @ApiResponse(responseCode = "201", description = "Desconto lançado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou nenhum ciclo ativo")
    public ResponseEntity<PenalidadeDto> lancar(@PathVariable Integer idUnidade,
                                                @RequestBody @Valid PenalidadeCriacaoDto dto,
                                                @AuthenticationPrincipal UsuarioDetalhesDto usuario) {
        if (usuario == null) return ResponseEntity.status(401).build();
        return ResponseEntity.status(201)
                .body(penalidadeService.lancar(idUnidade, dto, usuario.getIdClube(), usuario.getEmail()));
    }

    @DeleteMapping("/penalidades/{id}")
    @PreAuthorize("hasRole('DIRETORIA')")
    @Operation(summary = "Excluir desconto")
    public ResponseEntity<Void> remover(@PathVariable Integer id, @AuthenticationPrincipal UsuarioDetalhesDto usuario) {
        if (usuario == null) return ResponseEntity.status(401).build();
        penalidadeService.remover(id, usuario.getIdClube());
        return ResponseEntity.noContent().build();
    }
}
