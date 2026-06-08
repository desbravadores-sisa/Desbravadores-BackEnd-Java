package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.APIDesbravadores.dto.EvidenciaCreateDto;
import school.sptech.APIDesbravadores.dto.EvidenciaResponseDto;
import school.sptech.APIDesbravadores.dto.EvidenciaUpdateDto;
import school.sptech.APIDesbravadores.dto.UsuarioDetalhesDto;
import school.sptech.APIDesbravadores.service.EvidenciaService;

import java.util.List;

@RestController
@RequestMapping("/evidencias")
@Tag(name = "Evidências", description = "Endpoints para gerenciamento de evidências")
public class EvidenciaController {

    private final EvidenciaService evidenciaService;

    public EvidenciaController(EvidenciaService evidenciaService) {
        this.evidenciaService = evidenciaService;
    }

    @PostMapping
    @Operation(summary = "Anexar evidência", description = "Cria uma evidência para uma tarefa da unidade do conselheiro")
    @ApiResponse(responseCode = "201", description = "Evidência anexada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada para a unidade do conselheiro")
    @PreAuthorize("hasRole('CONSELHEIRO')")
    public ResponseEntity<EvidenciaResponseDto> create(
            @RequestBody @Valid EvidenciaCreateDto dto,
            @AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        EvidenciaResponseDto response = evidenciaService.create(dto, usuarioLogado.getIdUnidade());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as evidências", description = "Retorna as evidências do clube do diretor autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de evidências retornada com sucesso")
    @ApiResponse(responseCode = "204", description = "Nenhuma evidência encontrada")
    @PreAuthorize("hasRole('DIRETOR')")
    public ResponseEntity<List<EvidenciaResponseDto>> findAllByClube(
            @AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        List<EvidenciaResponseDto> response = evidenciaService.findAllByClube(usuarioLogado.getIdClube());
        if (response.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unidade")
    @Operation(summary = "Listar evidências da unidade", description = "Retorna as evidências da unidade vinculada ao conselheiro autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de evidências retornada com sucesso")
    @ApiResponse(responseCode = "204", description = "Nenhuma evidência encontrada para a unidade")
    @PreAuthorize("hasRole('CONSELHEIRO')")
    public ResponseEntity<List<EvidenciaResponseDto>> findAllByUnidade(
            @AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        List<EvidenciaResponseDto> response = evidenciaService.findAllByUnidade(usuarioLogado.getIdUnidade());
        if (response.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar evidência", description = "Atualiza uma evidência da unidade do conselheiro")
    @ApiResponse(responseCode = "200", description = "Evidência atualizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Evidência não encontrada")
    @PreAuthorize("hasRole('CONSELHEIRO')")
    public ResponseEntity<EvidenciaResponseDto> update(
            @Parameter(description = "ID da evidência", example = "1") @PathVariable Integer id,
            @RequestBody @Valid EvidenciaUpdateDto dto,
            @AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        EvidenciaResponseDto response = evidenciaService.update(id, dto, usuarioLogado.getIdUnidade());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar evidência", description = "Remove uma evidência da unidade do conselheiro quando a tarefa não está concluída")
    @ApiResponse(responseCode = "204", description = "Evidência deletada com sucesso")
    @ApiResponse(responseCode = "400", description = "Tarefa concluída não permite exclusão da evidência")
    @ApiResponse(responseCode = "404", description = "Evidência não encontrada")
    @PreAuthorize("hasRole('CONSELHEIRO')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da evidência", example = "1") @PathVariable Integer id,
            @AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        evidenciaService.delete(id, usuarioLogado.getIdUnidade());
        return ResponseEntity.status(204).build();
    }
}
