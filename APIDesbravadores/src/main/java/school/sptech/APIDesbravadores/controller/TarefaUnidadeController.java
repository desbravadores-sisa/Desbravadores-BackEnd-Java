package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.APIDesbravadores.dto.TarefaResponseDto;
import school.sptech.APIDesbravadores.dto.TarefaStatusUpdateDto;
import school.sptech.APIDesbravadores.service.TarefaService;

@RestController
@RequestMapping("/tarefas-unidades")
@Tag(name = "Tarefas Unidades", description = "Endpoints para status de tarefas por unidade")
public class TarefaUnidadeController {

    private final TarefaService tarefaService;

    public TarefaUnidadeController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @GetMapping("/{idTarefa}")
    @Operation(summary = "Visualizar status da tarefa", description = "Retorna o vínculo da tarefa com a unidade e seu status atual")
    @ApiResponse(responseCode = "200", description = "Status retornado com sucesso")
    @ApiResponse(responseCode = "404", description = "Tarefa ou vínculo com unidade não encontrado")
    public ResponseEntity<TarefaResponseDto> findStatusByTarefaId(
            @Parameter(description = "ID da tarefa", example = "1") @PathVariable Integer idTarefa) {
        TarefaResponseDto response = tarefaService.findStatusByTarefaId(idTarefa);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{idTarefa}/status")
    @Operation(summary = "Mover status da tarefa", description = "Altera o status da tarefa no quadro Kanban")
    @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Status inválido ou não fornecido")
    @ApiResponse(responseCode = "404", description = "Tarefa ou vínculo com unidade não encontrado")
    @PreAuthorize("hasRole('CONSELHEIRO')")
    public ResponseEntity<TarefaResponseDto> updateStatus(
            @Parameter(description = "ID da tarefa", example = "1") @PathVariable Integer idTarefa,
            @RequestBody @Valid TarefaStatusUpdateDto dto) {
        TarefaResponseDto response = tarefaService.updateStatus(idTarefa, dto.getStatus());
        return ResponseEntity.ok(response);
    }
}
