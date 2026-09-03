package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.dto.PerfilAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.PerfilCriacaoDto;
import school.sptech.APIDesbravadores.dto.PerfilResponseDto;
import school.sptech.APIDesbravadores.service.PerfilService;

import java.util.List;

@RestController
@RequestMapping("/perfis")
@Tag(name = "Perfis", description = "Endpoints para gerenciamento de perfis")
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @PostMapping
    @Operation(summary = "Criar um novo perfil", description = "Cria um perfil com as informações fornecidas")
    @ApiResponse(responseCode = "201", description = "Perfil criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    public ResponseEntity<PerfilResponseDto> create(@RequestBody @Valid PerfilCriacaoDto dto) {
        PerfilResponseDto response = perfilService.create(dto);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os perfis", description = "Retorna uma lista de todos os perfis cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de perfis retornada com sucesso")
    @ApiResponse(responseCode = "204", description = "Nenhum perfil encontrado")
    public ResponseEntity<List<PerfilResponseDto>> findAll() {
        List<PerfilResponseDto> response = perfilService.findAll();
        if (response.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar perfil por ID", description = "Retorna os detalhes de um perfil específico")
    @ApiResponse(responseCode = "200", description = "Perfil encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    public ResponseEntity<PerfilResponseDto> findById(
            @Parameter(description = "ID do perfil a ser buscado", example = "1") @PathVariable Integer id) {
        PerfilResponseDto response = perfilService.findById(id);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um perfil", description = "Atualiza os dados de um perfil existente")
    @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    public ResponseEntity<PerfilResponseDto> update(
            @Parameter(description = "ID do perfil a ser atualizado", example = "1") @PathVariable Integer id,
            @RequestBody @Valid PerfilAtualizacaoDto dto) {
        PerfilResponseDto response = perfilService.update(id, dto);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um perfil", description = "Remove um perfil do sistema")
    @ApiResponse(responseCode = "204", description = "Perfil deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do perfil a ser deletado", example = "1") @PathVariable Integer id) {
        perfilService.delete(id);
        return ResponseEntity.status(204).build();
    }
}
