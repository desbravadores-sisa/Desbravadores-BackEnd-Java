package school.sptech.api_tasks.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.api_tasks.dto.TarefaCreateDto;
import school.sptech.api_tasks.dto.TarefaResponseDto;
import school.sptech.api_tasks.dto.TarefaUpdateDto;
import school.sptech.api_tasks.service.TarefaService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tarefas")
@Tag(name = "Tarefas", description = "Endpoints para gerenciamento de tarefas")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @PostMapping
    @Operation(summary = "Criar uma nova tarefa", description = "Cria uma tarefa com as informações fornecidas")
    @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    public ResponseEntity<TarefaResponseDto> create(@RequestBody @Valid TarefaCreateDto dto) {
        TarefaResponseDto response = tarefaService.create(dto);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as tarefas", description = "Retorna uma lista de todas as tarefas cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso")
    @ApiResponse(responseCode = "204", description = "Nenhuma tarefa encontrada")
    public ResponseEntity<List<TarefaResponseDto>> findAll() {
        List<TarefaResponseDto> response = tarefaService.findAll();
        if (response.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tarefa por ID", description = "Retorna os detalhes de uma tarefa específica")
    @ApiResponse(responseCode = "200", description = "Tarefa encontrada com sucesso")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public ResponseEntity<TarefaResponseDto> findById(
            @Parameter(description = "ID da tarefa a ser buscada", example = "1") @PathVariable Integer id) {
        TarefaResponseDto response = tarefaService.findById(id);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma tarefa", description = "Atualiza os dados de uma tarefa existente")
    @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    public ResponseEntity<TarefaResponseDto> update(
            @Parameter(description = "ID da tarefa a ser atualizada", example = "1") @PathVariable Integer id,
            @RequestBody @Valid TarefaUpdateDto dto) {
        TarefaResponseDto response = tarefaService.update(id, dto);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma tarefa", description = "Remove uma tarefa do sistema")
    @ApiResponse(responseCode = "204", description = "Tarefa deletada com sucesso")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da tarefa a ser deletada", example = "1") @PathVariable Integer id) {
        tarefaService.delete(id);
        return ResponseEntity.status(204).build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status da tarefa (Kanban)", description = "Altera o status de uma tarefa no quadro Kanban")
    @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    @ApiResponse(responseCode = "400", description = "Status inválido ou não fornecido")
    public ResponseEntity<TarefaResponseDto> updateStatus(
            @Parameter(description = "ID da tarefa a ter o status alterado", example = "1") @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null) {
            return ResponseEntity.status(400).build();
        }
        TarefaResponseDto response = tarefaService.updateStatus(id, status);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/kanban")
    @Operation(summary = "Listar tarefas agrupadas por status", description = "Retorna as tarefas organizadas pelas colunas do Kanban")
    @ApiResponse(responseCode = "200", description = "Quadro Kanban retornado com sucesso")
    @ApiResponse(responseCode = "204", description = "Nenhuma tarefa encontrada para o Kanban")
    public ResponseEntity<Map<String, List<TarefaResponseDto>>> getKanban() {
        Map<String, List<TarefaResponseDto>> response = tarefaService.getKanban();
        if (response.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.status(200).body(response);
    }
}
