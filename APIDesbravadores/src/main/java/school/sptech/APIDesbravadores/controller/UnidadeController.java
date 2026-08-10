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
import school.sptech.APIDesbravadores.dto.UnidadeAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.UnidadeCriacaoDto;
import school.sptech.APIDesbravadores.dto.UnidadeResponseDto;
import school.sptech.APIDesbravadores.dto.UsuarioDetalhesDto;
import school.sptech.APIDesbravadores.service.UnidadeService;

import java.util.List;

@RestController
@RequestMapping("/unidades")
@Tag(name = "Unidades", description = "Endpoints para gerenciamento das unidades do clube")
public class UnidadeController {
    private final UnidadeService unidadeService;

    public UnidadeController(UnidadeService unidadeService) {
        this.unidadeService = unidadeService;
    }


    /*
    * =========================================================================
    * Permissão Diretor
    * =========================================================================
    * */
    @GetMapping("/diretor")
    @PreAuthorize("hasRole('DIRETOR')")
    @Operation(summary = "Listar unidades do clube", description = "Retorna todas as unidades do clube do diretor autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de unidades retornada com sucesso")
    @ApiResponse(responseCode = "204", description = "O clube não possui unidades cadastradas")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "403", description = "Usuário não possui a permissão DIRETOR")
    @ApiResponse(responseCode = "404", description = "Clube não encontrado")
    public ResponseEntity<List<UnidadeResponseDto>> listarUnidades(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado){
        Integer idClube = usuariologado.getIdClube();
        List<UnidadeResponseDto> unidades = unidadeService.listaUnidade(idClube);
        if (unidades.isEmpty()){
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(unidades);
    }

    @PostMapping
    @PreAuthorize("hasRole('DIRETOR')")
    @Operation(summary = "Cadastrar uma nova unidade", description = "Cria uma unidade vinculada ao clube do diretor autenticado")
    @ApiResponse(responseCode = "201", description = "Unidade criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "403", description = "Usuário não possui a permissão DIRETOR")
    public ResponseEntity<UnidadeResponseDto> cadastrarUnidade(@RequestBody @Valid UnidadeCriacaoDto request, @AuthenticationPrincipal UsuarioDetalhesDto usuariologado){
        Integer idClube = usuariologado.getIdClube();
        return ResponseEntity.status(201).body(unidadeService.cadastrarUnidade(request,idClube));
    }

    @PutMapping
    @PreAuthorize("hasRole('DIRETOR')")
    @Operation(summary = "Atualizar uma unidade", description = "Atualiza os dados de uma unidade existente")
    @ApiResponse(responseCode = "200", description = "Unidade atualizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "403", description = "Usuário não possui a permissão DIRETOR")
    @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    public ResponseEntity<UnidadeResponseDto> atualizarUnidade(@RequestBody @Valid UnidadeAtualizacaoDto request){
        return ResponseEntity.ok(unidadeService.atualizarUnidade(request));
    }

    @DeleteMapping("/{idUnidade}")
    @PreAuthorize("hasRole('DIRETOR')")
    @Operation(summary = "Deletar uma unidade", description = "Remove uma unidade do clube e desvincula os usuários que pertenciam a ela")
    @ApiResponse(responseCode = "204", description = "Unidade deletada com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "403", description = "Usuário não possui a permissão DIRETOR")
    @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    public ResponseEntity<Void> deletarUnidade(
            @Parameter(description = "ID da unidade a ser deletada", example = "1") @PathVariable Integer idUnidade){
        unidadeService.deletarUnidade(idUnidade);
        return ResponseEntity.status(204).build();
    }


    /*
     * =========================================================================
     * Permissão Conselheiro
     * =========================================================================
     * */

    @GetMapping("/conselheiro")
    @PreAuthorize("hasRole('CONSELHEIRO')")
    @Operation(summary = "Buscar a unidade do conselheiro", description = "Retorna os dados da unidade vinculada ao conselheiro autenticado")
    @ApiResponse(responseCode = "200", description = "Unidade encontrada com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "403", description = "Usuário não possui a permissão CONSELHEIRO")
    @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    public ResponseEntity<UnidadeResponseDto> buscarUnidadeConselheiro(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado){
        Integer idUnidade = usuariologado.getIdUnidade();
        UnidadeResponseDto unidade =  unidadeService.buscarUnidadePorId(idUnidade);
        System.out.println("A unidade:" + unidade);
        return ResponseEntity.ok(unidadeService.buscarUnidadePorId(idUnidade));
    }
}
