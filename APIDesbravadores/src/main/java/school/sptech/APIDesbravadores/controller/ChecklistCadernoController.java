package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.dto.ChecklistCadernoAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.ChecklistCadernoCriacaoDto;
import school.sptech.APIDesbravadores.dto.ChecklistCadernoResponseDto;
import school.sptech.APIDesbravadores.service.ChecklistCadernoService;

import java.util.List;

@RestController
@RequestMapping("/checklists-caderno")
@Tag(name = "Checklists do Caderno", description = "Endpoints para gerenciamento dos checklists do caderno")
public class ChecklistCadernoController {

    private final ChecklistCadernoService checklistCadernoService;

    public ChecklistCadernoController(ChecklistCadernoService checklistCadernoService) {
        this.checklistCadernoService = checklistCadernoService;
    }

    @PostMapping
    @Operation(summary = "Criar um novo checklist", description = "Cria um checklist de caderno com as informações fornecidas")
    @ApiResponse(responseCode = "201", description = "Checklist criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    public ResponseEntity<ChecklistCadernoResponseDto> create(@RequestBody @Valid ChecklistCadernoCriacaoDto dto) {
        ChecklistCadernoResponseDto response = checklistCadernoService.create(dto);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os checklists", description = "Retorna uma lista de todos os checklists cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de checklists retornada com sucesso")
    @ApiResponse(responseCode = "204", description = "Nenhum checklist encontrado")
    public ResponseEntity<List<ChecklistCadernoResponseDto>> findAll() {
        List<ChecklistCadernoResponseDto> response = checklistCadernoService.findAll();
        if (response.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar checklist por ID", description = "Retorna os detalhes de um checklist específico")
    @ApiResponse(responseCode = "200", description = "Checklist encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Checklist não encontrado")
    public ResponseEntity<ChecklistCadernoResponseDto> findById(
            @Parameter(description = "ID do checklist a ser buscado", example = "1") @PathVariable Integer id) {
        ChecklistCadernoResponseDto response = checklistCadernoService.findById(id);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um checklist", description = "Atualiza os dados de um checklist existente")
    @ApiResponse(responseCode = "200", description = "Checklist atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Checklist não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    public ResponseEntity<ChecklistCadernoResponseDto> update(
            @Parameter(description = "ID do checklist a ser atualizado", example = "1") @PathVariable Integer id,
            @RequestBody @Valid ChecklistCadernoAtualizacaoDto dto) {
        ChecklistCadernoResponseDto response = checklistCadernoService.update(id, dto);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um checklist", description = "Remove um checklist do sistema")
    @ApiResponse(responseCode = "204", description = "Checklist deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Checklist não encontrado")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do checklist a ser deletado", example = "1") @PathVariable Integer id) {
        checklistCadernoService.delete(id);
        return ResponseEntity.status(204).build();
    }
}
