package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.dto.DesbravadorAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.DesbravadorCriacaoDto;
import school.sptech.APIDesbravadores.dto.DesbravadorResponseDto;
import school.sptech.APIDesbravadores.service.DesbravadorService;

import java.util.List;

@RestController
@RequestMapping("/desbravadores")
@Tag(name = "Desbravadores", description = "Endpoints para gerenciamento de desbravadores")
public class DesbravadorController {

    private final DesbravadorService desbravadorService;

    public DesbravadorController(DesbravadorService desbravadorService) {
        this.desbravadorService = desbravadorService;
    }

    @PostMapping
    @Operation(summary = "Criar um novo desbravador", description = "Cria um desbravador com as informações fornecidas")
    @ApiResponse(responseCode = "201", description = "Desbravador criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Clube ou unidade não encontrados")
    public ResponseEntity<DesbravadorResponseDto> create(@RequestBody @Valid DesbravadorCriacaoDto dto) {
        DesbravadorResponseDto response = desbravadorService.create(dto);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar desbravadores", description = "Retorna todos os desbravadores, opcionalmente filtrados por clube")
    @ApiResponse(responseCode = "200", description = "Lista de desbravadores retornada com sucesso")
    @ApiResponse(responseCode = "204", description = "Nenhum desbravador encontrado")
    public ResponseEntity<List<DesbravadorResponseDto>> findAll(
            @Parameter(description = "ID do clube para filtrar os desbravadores", example = "1")
            @RequestParam(required = false) Integer idClube) {
        List<DesbravadorResponseDto> response = desbravadorService.findAll(idClube);
        if (response.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar desbravador por ID", description = "Retorna os detalhes de um desbravador específico")
    @ApiResponse(responseCode = "200", description = "Desbravador encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Desbravador não encontrado")
    public ResponseEntity<DesbravadorResponseDto> findById(
            @Parameter(description = "ID do desbravador a ser buscado", example = "1") @PathVariable Integer id) {
        DesbravadorResponseDto response = desbravadorService.findById(id);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um desbravador", description = "Atualiza os dados de um desbravador existente")
    @ApiResponse(responseCode = "200", description = "Desbravador atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Desbravador, clube ou unidade não encontrados")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    public ResponseEntity<DesbravadorResponseDto> update(
            @Parameter(description = "ID do desbravador a ser atualizado", example = "1") @PathVariable Integer id,
            @RequestBody @Valid DesbravadorAtualizacaoDto dto) {
        DesbravadorResponseDto response = desbravadorService.update(id, dto);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um desbravador", description = "Remove um desbravador do sistema")
    @ApiResponse(responseCode = "204", description = "Desbravador deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Desbravador não encontrado")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do desbravador a ser deletado", example = "1") @PathVariable Integer id) {
        desbravadorService.delete(id);
        return ResponseEntity.status(204).build();
    }
}
