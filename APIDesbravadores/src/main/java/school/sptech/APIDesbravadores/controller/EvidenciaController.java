package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.dto.UsuarioDetalhesDto;
import school.sptech.APIDesbravadores.dto.EvidenciaAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.EvidenciaCriacaoDto;
import school.sptech.APIDesbravadores.dto.EvidenciaResponseDto;
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
    @Operation(summary = "Criar uma nova evidência", description = "Cria uma evidência com as informações fornecidas")
    @ApiResponse(responseCode = "201", description = "Evidência criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    public ResponseEntity<EvidenciaResponseDto> create(@RequestBody @Valid EvidenciaCriacaoDto dto) {
        EvidenciaResponseDto response = evidenciaService.create(dto);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as evidências", description = "Retorna uma lista de todas as evidências cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de evidências retornada com sucesso")
    @ApiResponse(responseCode = "204", description = "Nenhuma evidência encontrada")
    public ResponseEntity<List<EvidenciaResponseDto>> findAll(@AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        List<EvidenciaResponseDto> response = usuarioLogado != null && usuarioLogado.getIdClube() != null
                ? evidenciaService.findAllByClube(usuarioLogado.getIdClube())
                : evidenciaService.findAll();
        if (response.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar evidência por ID", description = "Retorna os detalhes de uma evidência específica")
    @ApiResponse(responseCode = "200", description = "Evidência encontrada com sucesso")
    @ApiResponse(responseCode = "404", description = "Evidência não encontrada")
    public ResponseEntity<EvidenciaResponseDto> findById(
            @Parameter(description = "ID da evidência a ser buscada", example = "1") @PathVariable Integer id) {
        EvidenciaResponseDto response = evidenciaService.findById(id);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma evidência", description = "Atualiza os dados de uma evidência existente")
    @ApiResponse(responseCode = "200", description = "Evidência atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Evidência não encontrada")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    public ResponseEntity<EvidenciaResponseDto> update(
            @Parameter(description = "ID da evidência a ser atualizada", example = "1") @PathVariable Integer id,
            @RequestBody @Valid EvidenciaAtualizacaoDto dto) {
        EvidenciaResponseDto response = evidenciaService.update(id, dto);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma evidência", description = "Remove uma evidência do sistema")
    @ApiResponse(responseCode = "204", description = "Evidência deletada com sucesso")
    @ApiResponse(responseCode = "404", description = "Evidência não encontrada")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da evidência a ser deletada", example = "1") @PathVariable Integer id) {
        evidenciaService.delete(id);
        return ResponseEntity.status(204).build();
    }

    @PostMapping(value = "/envio", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('CONSELHEIRO')")
    @Operation(summary = "Enviar evidência (arquivo) de uma tarefa de clube",
            description = "Conselheiro envia foto (JPG/PNG/WEBP) ou PDF de até 5 MB; a tarefa vai para EM REVISAO")
    @ApiResponse(responseCode = "201", description = "Evidência enviada")
    @ApiResponse(responseCode = "400", description = "Arquivo inválido, tarefa de caderno ou já em revisão")
    public ResponseEntity<EvidenciaResponseDto> enviar(
            @RequestParam Integer idTarefa,
            @RequestParam("arquivo") org.springframework.web.multipart.MultipartFile arquivo,
            @RequestParam(required = false) String comentario,
            @AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.status(201).body(evidenciaService.enviar(idTarefa, arquivo, comentario,
                usuarioLogado.getIdUnidade(), usuarioLogado.getIdClube()));
    }
}
