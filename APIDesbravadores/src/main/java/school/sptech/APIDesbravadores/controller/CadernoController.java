package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.dto.CadernoAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.CadernoCriacaoDto;
import school.sptech.APIDesbravadores.dto.CadernoResponseDto;
import school.sptech.APIDesbravadores.service.CadernoService;

import java.util.List;

@RestController
@RequestMapping("/cadernos")
@Tag(name = "Cadernos", description = "Endpoints para gerenciamento de cadernos")
public class CadernoController {

    private final CadernoService cadernoService;

    public CadernoController(CadernoService cadernoService) {
        this.cadernoService = cadernoService;
    }

    @PostMapping
    @Operation(summary = "Criar um novo caderno", description = "Cria um caderno com as informações fornecidas")
    @ApiResponse(responseCode = "201", description = "Caderno criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Clube não encontrado")
    public ResponseEntity<CadernoResponseDto> create(@RequestBody @Valid CadernoCriacaoDto dto) {
        CadernoResponseDto response = cadernoService.create(dto);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar cadernos", description = "Retorna todos os cadernos, opcionalmente filtrados por clube")
    @ApiResponse(responseCode = "200", description = "Lista de cadernos retornada com sucesso")
    @ApiResponse(responseCode = "204", description = "Nenhum caderno encontrado")
    public ResponseEntity<List<CadernoResponseDto>> findAll(
            @Parameter(description = "ID do clube para filtrar os cadernos", example = "1")
            @RequestParam(required = false) Integer idClube) {
        List<CadernoResponseDto> response = cadernoService.findAll(idClube);
        if (response.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar caderno por ID", description = "Retorna os detalhes de um caderno específico")
    @ApiResponse(responseCode = "200", description = "Caderno encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Caderno não encontrado")
    public ResponseEntity<CadernoResponseDto> findById(
            @Parameter(description = "ID do caderno a ser buscado", example = "1") @PathVariable Integer id) {
        CadernoResponseDto response = cadernoService.findById(id);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um caderno", description = "Atualiza os dados de um caderno existente")
    @ApiResponse(responseCode = "200", description = "Caderno atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Caderno ou clube não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    public ResponseEntity<CadernoResponseDto> update(
            @Parameter(description = "ID do caderno a ser atualizado", example = "1") @PathVariable Integer id,
            @RequestBody @Valid CadernoAtualizacaoDto dto) {
        CadernoResponseDto response = cadernoService.update(id, dto);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um caderno", description = "Remove um caderno do sistema")
    @ApiResponse(responseCode = "204", description = "Caderno deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Caderno não encontrado")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do caderno a ser deletado", example = "1") @PathVariable Integer id) {
        cadernoService.delete(id);
        return ResponseEntity.status(204).build();
    }
}
