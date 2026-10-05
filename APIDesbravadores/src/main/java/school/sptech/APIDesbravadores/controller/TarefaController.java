package school.sptech.APIDesbravadores.controller;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.dto.*;
import school.sptech.APIDesbravadores.service.TarefaService;
import java.util.*;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {
    private final TarefaService tarefaService;
    public TarefaController(TarefaService tarefaService) { this.tarefaService = tarefaService; }
    @PostMapping @PreAuthorize("@acesso.diretoria()")
    public ResponseEntity<TarefaResponseDto> create(@RequestBody @Valid TarefaCreateDto dto) {
        return ResponseEntity.status(201).body(tarefaService.create(dto));
    }
    @GetMapping @PreAuthorize("@acesso.diretoria()")
    public ResponseEntity<List<TarefaResponseDto>> findAll() {
        var result = tarefaService.findAll();
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }
    @GetMapping("/{id}")
    public TarefaResponseDto findById(@PathVariable Integer id) { return tarefaService.findById(id); }
    @PutMapping("/{id}") @PreAuthorize("@acesso.diretoria()")
    public TarefaResponseDto update(@PathVariable Integer id, @RequestBody @Valid TarefaUpdateDto dto) {
        return tarefaService.update(id, dto);
    }
    @DeleteMapping("/{id}") @PreAuthorize("@acesso.diretoria()")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        tarefaService.delete(id); return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/status") @PreAuthorize("hasRole('CONSELHEIRO')")
    public TarefaResponseDto updateStatus(@PathVariable Integer id, @RequestBody @Valid TarefaStatusUpdateDto dto) {
        return tarefaService.updateStatus(id, dto.getStatus());
    }
    @GetMapping("/kanban") @PreAuthorize("hasRole('CONSELHEIRO')")
    public ResponseEntity<Map<String, List<TarefaResponseDto>>> getKanban() {
        var result = tarefaService.getKanban();
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }
}
