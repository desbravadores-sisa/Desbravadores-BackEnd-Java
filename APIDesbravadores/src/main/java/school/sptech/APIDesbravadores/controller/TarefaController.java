package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.dto.UsuarioDetalhesDto;
import school.sptech.APIDesbravadores.dto.TarefaCreateDto;
import school.sptech.APIDesbravadores.dto.TarefaResponseDto;
import school.sptech.APIDesbravadores.dto.TarefaStatusUpdateDto;
import school.sptech.APIDesbravadores.dto.TarefaUpdateDto;
import school.sptech.APIDesbravadores.service.TarefaService;

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
    @PreAuthorize("hasRole('DIRETORIA')")
    public ResponseEntity<TarefaResponseDto> create(@RequestBody @Valid TarefaCreateDto dto,
                                                    @AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        // O front não envia o clube: ele é derivado do usuário autenticado.
        if (dto.getFkClube() == null && usuarioLogado != null) {
            dto.setFkClube(usuarioLogado.getIdClube());
        }
        TarefaResponseDto response = tarefaService.create(dto);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as tarefas", description = "Retorna uma lista de todas as tarefas cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso")
    @ApiResponse(responseCode = "204", description = "Nenhuma tarefa encontrada")
    public ResponseEntity<List<TarefaResponseDto>> findAll(@AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        List<TarefaResponseDto> response = usuarioLogado != null && usuarioLogado.getIdClube() != null
                ? tarefaService.findAllByClube(usuarioLogado.getIdClube())
                : tarefaService.findAll();
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
    @PreAuthorize("hasRole('DIRETORIA')")
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
    @PreAuthorize("hasRole('DIRETORIA')")
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
    @PreAuthorize("hasRole('CONSELHEIRO')")
    public ResponseEntity<TarefaResponseDto> updateStatus(
            @Parameter(description = "ID da tarefa a ter o status alterado", example = "1") @PathVariable Integer id,
            @RequestBody @Valid TarefaStatusUpdateDto dto,
            @AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(401).build();
        }
        TarefaResponseDto response = tarefaService.updateStatusDaUnidade(id, usuarioLogado.getIdUnidade(), dto.getStatus());
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/kanban")
    @Operation(summary = "Kanban da unidade do conselheiro", description = "Tarefas da unidade do conselheiro logado no ciclo ativo, agrupadas por status (A FAZER, EM ANDAMENTO, EM REVISAO, CONCLUIDA). Colunas vazias vêm como lista vazia.")
    @ApiResponse(responseCode = "200", description = "Quadro Kanban retornado com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "403", description = "Usuário não é conselheiro")
    @PreAuthorize("hasRole('CONSELHEIRO')")
    public ResponseEntity<Map<String, List<TarefaResponseDto>>> getKanban(
            @AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(tarefaService.getKanbanDaUnidade(usuarioLogado.getIdUnidade(), usuarioLogado.getIdClube()));
    }
}
