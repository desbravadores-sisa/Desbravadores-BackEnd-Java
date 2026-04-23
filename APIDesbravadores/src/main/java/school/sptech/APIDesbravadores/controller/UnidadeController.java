package school.sptech.APIDesbravadores.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping
    @PreAuthorize("hasRole('DIRETOR')") // O Spring Security vai olhar a pulseira antes de rodar essa linha
    public ResponseEntity<List<UnidadeResponseDto>> ListarUnidades(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado){
        Integer idClube = usuariologado.getIdClube();
        return ResponseEntity.ok(unidadeService.listaUnidade(idClube));

    }
}
