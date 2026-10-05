package school.sptech.APIDesbravadores.controller;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.dto.*;
import school.sptech.APIDesbravadores.service.TarefaService;

@RestController
@RequestMapping("/tarefas-unidades")
@PreAuthorize("hasRole('CONSELHEIRO')")
public class TarefaUnidadeController {
    private final TarefaService tarefaService;
    public TarefaUnidadeController(TarefaService tarefaService) { this.tarefaService = tarefaService; }
    @GetMapping("/{idTarefa}")
    public TarefaResponseDto findStatusByTarefaId(@PathVariable Integer idTarefa) {
        return tarefaService.findStatusByTarefaId(idTarefa);
    }
    @PutMapping("/{idTarefa}/status")
    public TarefaResponseDto updateStatus(@PathVariable Integer idTarefa, @RequestBody @Valid TarefaStatusUpdateDto dto) {
        return tarefaService.updateStatus(idTarefa, dto.getStatus());
    }
}
