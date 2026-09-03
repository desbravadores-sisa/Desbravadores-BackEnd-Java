package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.dto.CicloAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.CicloCriacaoDto;
import school.sptech.APIDesbravadores.dto.CicloResponseDto;
import school.sptech.APIDesbravadores.service.CicloService;

import java.util.List;

@RestController
@RequestMapping("/ciclos")
@Tag(name = "Ciclos", description = "Endpoints para gerenciamento de ciclos")
public class CicloController {

    private final CicloService cicloService;

    public CicloController(CicloService cicloService) {
        this.cicloService = cicloService;
    }

    @PostMapping
    @Operation(summary = "Criar um novo ciclo", description = "Cria um ciclo com as informações fornecidas")
    @ApiResponse(responseCode = "201", description = "Ciclo criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Clube não encontrado")
    public ResponseEntity<CicloResponseDto> create(@RequestBody @Valid CicloCriacaoDto dto) {
        CicloResponseDto response = cicloService.create(dto);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar ciclos", description = "Retorna todos os ciclos, opcionalmente filtrados por clube")
    @ApiResponse(responseCode = "200", description = "Lista de ciclos retornada com sucesso")
    @ApiResponse(responseCode = "204", description = "Nenhum ciclo encontrado")
    public ResponseEntity<List<CicloResponseDto>> findAll(
            @Parameter(description = "ID do clube para filtrar os ciclos", example = "1")
            @RequestParam(required = false) Integer idClube) {
        List<CicloResponseDto> response = cicloService.findAll(idClube);
        if (response.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ciclo por ID", description = "Retorna os detalhes de um ciclo específico")
    @ApiResponse(responseCode = "200", description = "Ciclo encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Ciclo não encontrado")
    public ResponseEntity<CicloResponseDto> findById(
            @Parameter(description = "ID do ciclo a ser buscado", example = "1") @PathVariable Integer id) {
        CicloResponseDto response = cicloService.findById(id);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um ciclo", description = "Atualiza os dados de um ciclo existente")
    @ApiResponse(responseCode = "200", description = "Ciclo atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Ciclo ou clube não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    public ResponseEntity<CicloResponseDto> update(
            @Parameter(description = "ID do ciclo a ser atualizado", example = "1") @PathVariable Integer id,
            @RequestBody @Valid CicloAtualizacaoDto dto) {
        CicloResponseDto response = cicloService.update(id, dto);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um ciclo", description = "Remove um ciclo do sistema")
    @ApiResponse(responseCode = "204", description = "Ciclo deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Ciclo não encontrado")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do ciclo a ser deletado", example = "1") @PathVariable Integer id) {
        cicloService.delete(id);
        return ResponseEntity.status(204).build();
    }
}
