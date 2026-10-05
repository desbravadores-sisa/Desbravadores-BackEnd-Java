package school.sptech.APIDesbravadores.controller;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.dto.*;
import school.sptech.APIDesbravadores.service.EvidenciaService;
import java.util.List;

@RestController
@RequestMapping("/evidencias")
public class EvidenciaController {
    private final EvidenciaService evidenciaService;
    public EvidenciaController(EvidenciaService evidenciaService) { this.evidenciaService = evidenciaService; }
    @PostMapping @PreAuthorize("hasRole('CONSELHEIRO')")
    public ResponseEntity<EvidenciaResponseDto> create(@RequestBody @Valid EvidenciaCreateDto dto,
            @AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        return ResponseEntity.status(201).body(evidenciaService.create(dto, usuarioLogado.getIdUnidade()));
    }
    @GetMapping @PreAuthorize("@acesso.diretoria()")
    public ResponseEntity<List<EvidenciaResponseDto>> findAllByClube(@AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        var result = evidenciaService.findAllByClube(usuarioLogado.getIdClube());
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }
    @GetMapping("/unidade") @PreAuthorize("hasRole('CONSELHEIRO')")
    public ResponseEntity<List<EvidenciaResponseDto>> findAllByUnidade(@AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        var result = evidenciaService.findAllByUnidade(usuarioLogado.getIdUnidade());
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }
    @PutMapping("/{id}") @PreAuthorize("hasRole('CONSELHEIRO')")
    public EvidenciaResponseDto update(@PathVariable Integer id, @RequestBody @Valid EvidenciaUpdateDto dto,
            @AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        return evidenciaService.update(id, dto, usuarioLogado.getIdUnidade());
    }
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('CONSELHEIRO')")
    public ResponseEntity<Void> delete(@PathVariable Integer id, @AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        evidenciaService.delete(id, usuarioLogado.getIdUnidade()); return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/approve") @PreAuthorize("@acesso.diretoria()")
    public EvidenciaResponseDto aprovar(@PathVariable Integer id, @RequestBody @Valid AprovacaoRequestDto dto) {
        return evidenciaService.aprovar(id, dto);
    }
    @PatchMapping("/{id}/correction") @PreAuthorize("@acesso.diretoria()")
    public EvidenciaResponseDto corrigir(@PathVariable Integer id, @RequestBody @Valid CorrecaoRequestDto dto) {
        return evidenciaService.solicitarCorrecao(id, dto);
    }
}
