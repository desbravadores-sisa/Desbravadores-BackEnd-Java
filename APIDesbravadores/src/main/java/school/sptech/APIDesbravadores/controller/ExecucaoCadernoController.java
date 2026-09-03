package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.dto.ExecucaoCadernoAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.ExecucaoCadernoCriacaoDto;
import school.sptech.APIDesbravadores.dto.ExecucaoCadernoResponseDto;
import school.sptech.APIDesbravadores.service.ExecucaoCadernoService;

import java.util.List;

@RestController
@RequestMapping("/execucoes-caderno")
@Tag(name = "Execuções do Caderno", description = "Endpoints para gerenciamento das execuções do caderno")
public class ExecucaoCadernoController {

    private final ExecucaoCadernoService execucaoCadernoService;

    public ExecucaoCadernoController(ExecucaoCadernoService execucaoCadernoService) {
        this.execucaoCadernoService = execucaoCadernoService;
    }

    @PostMapping
    @Operation(summary = "Criar uma nova execução de caderno", description = "Cria uma execução de caderno com as informações fornecidas")
    @ApiResponse(responseCode = "201", description = "Execução de caderno criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    public ResponseEntity<ExecucaoCadernoResponseDto> create(@RequestBody @Valid ExecucaoCadernoCriacaoDto dto) {
        ExecucaoCadernoResponseDto response = execucaoCadernoService.create(dto);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as execuções de caderno", description = "Retorna uma lista de todas as execuções cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de execuções retornada com sucesso")
    @ApiResponse(responseCode = "204", description = "Nenhuma execução encontrada")
    public ResponseEntity<List<ExecucaoCadernoResponseDto>> findAll() {
        List<ExecucaoCadernoResponseDto> response = execucaoCadernoService.findAll();
        if (response.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar execução de caderno por ID", description = "Retorna os detalhes de uma execução específica")
    @ApiResponse(responseCode = "200", description = "Execução encontrada com sucesso")
    @ApiResponse(responseCode = "404", description = "Execução não encontrada")
    public ResponseEntity<ExecucaoCadernoResponseDto> findById(
            @Parameter(description = "ID da execução a ser buscada", example = "1") @PathVariable Integer id) {
        ExecucaoCadernoResponseDto response = execucaoCadernoService.findById(id);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma execução de caderno", description = "Atualiza os dados de uma execução existente")
    @ApiResponse(responseCode = "200", description = "Execução atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Execução não encontrada")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    public ResponseEntity<ExecucaoCadernoResponseDto> update(
            @Parameter(description = "ID da execução a ser atualizada", example = "1") @PathVariable Integer id,
            @RequestBody @Valid ExecucaoCadernoAtualizacaoDto dto) {
        ExecucaoCadernoResponseDto response = execucaoCadernoService.update(id, dto);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma execução de caderno", description = "Remove uma execução do sistema")
    @ApiResponse(responseCode = "204", description = "Execução deletada com sucesso")
    @ApiResponse(responseCode = "404", description = "Execução não encontrada")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da execução a ser deletada", example = "1") @PathVariable Integer id) {
        execucaoCadernoService.delete(id);
        return ResponseEntity.status(204).build();
    }
}
