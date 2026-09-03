package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.dto.ClubeAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.ClubeCriacaoDto;
import school.sptech.APIDesbravadores.dto.ClubeResponseDto;
import school.sptech.APIDesbravadores.service.ClubeService;

import java.util.List;

@RestController
@RequestMapping("/clubes")
@Tag(name = "Clubes", description = "Endpoints para gerenciamento de clubes")
public class ClubeController {

    private final ClubeService clubeService;

    public ClubeController(ClubeService clubeService) {
        this.clubeService = clubeService;
    }

    @PostMapping
    @Operation(summary = "Criar um novo clube", description = "Cria um clube com as informações fornecidas")
    @ApiResponse(responseCode = "201", description = "Clube criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    public ResponseEntity<ClubeResponseDto> create(@RequestBody @Valid ClubeCriacaoDto dto) {
        ClubeResponseDto response = clubeService.create(dto);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os clubes", description = "Retorna uma lista de todos os clubes cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de clubes retornada com sucesso")
    @ApiResponse(responseCode = "204", description = "Nenhum clube encontrado")
    public ResponseEntity<List<ClubeResponseDto>> findAll() {
        List<ClubeResponseDto> response = clubeService.findAll();
        if (response.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar clube por ID", description = "Retorna os detalhes de um clube específico")
    @ApiResponse(responseCode = "200", description = "Clube encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Clube não encontrado")
    public ResponseEntity<ClubeResponseDto> findById(
            @Parameter(description = "ID do clube a ser buscado", example = "1") @PathVariable Integer id) {
        ClubeResponseDto response = clubeService.findById(id);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um clube", description = "Atualiza os dados de um clube existente")
    @ApiResponse(responseCode = "200", description = "Clube atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Clube não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    public ResponseEntity<ClubeResponseDto> update(
            @Parameter(description = "ID do clube a ser atualizado", example = "1") @PathVariable Integer id,
            @RequestBody @Valid ClubeAtualizacaoDto dto) {
        ClubeResponseDto response = clubeService.update(id, dto);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um clube", description = "Remove um clube do sistema")
    @ApiResponse(responseCode = "204", description = "Clube deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Clube não encontrado")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do clube a ser deletado", example = "1") @PathVariable Integer id) {
        clubeService.delete(id);
        return ResponseEntity.status(204).build();
    }
}
