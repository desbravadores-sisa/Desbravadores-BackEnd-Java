package school.sptech.APIDesbravadores.controller;

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
    public ResponseEntity<List<UnidadeResponseDto>> listarUnidades(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado){
        Integer idClube = usuariologado.getIdClube();
        return ResponseEntity.ok(unidadeService.listaUnidade(idClube));

    }

    @PostMapping
    @PreAuthorize("hasRole('DIRETOR')")
    public ResponseEntity<UnidadeResponseDto> cadastrarUnidade(@RequestBody @Valid UnidadeCriacaoDto request, @AuthenticationPrincipal UsuarioDetalhesDto usuariologado){
        Integer idClube = usuariologado.getIdClube();
        return ResponseEntity.status(201).body(unidadeService.cadastrarUnidade(request,idClube));
    }

    @PutMapping
    @PreAuthorize("hasRole('DIRETOR')")
    public ResponseEntity<UnidadeResponseDto> atualizarUnidade(@RequestBody @Valid UnidadeAtualizacaoDto request){
        return ResponseEntity.ok(unidadeService.atualizarUnidade(request));
    }

    @DeleteMapping("/{idUnidade}")
    @PreAuthorize("hasRole('DIRETOR')")
    public ResponseEntity<Void> deletarUnidade(@PathVariable Integer idUnidade){
        System.out.println("Cai no método");
        unidadeService.deletarUnidade(idUnidade);
        return ResponseEntity.ok().build();
    }


    /*
     * =========================================================================
     * Permissão Conselheiro
     * =========================================================================
     * */

    @GetMapping("/conselheiro")
    @PreAuthorize("hasRole('CONSELHEIRO')")
    public ResponseEntity<UnidadeResponseDto> buscarUnidadeConselheiro(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado){
        Integer idUnidade = usuariologado.getIdUnidade();
        UnidadeResponseDto unidade =  unidadeService.buscarUnidadePorId(idUnidade);
        System.out.println("A unidade:" + unidade);
        return ResponseEntity.ok(unidadeService.buscarUnidadePorId(idUnidade));
    }
}
