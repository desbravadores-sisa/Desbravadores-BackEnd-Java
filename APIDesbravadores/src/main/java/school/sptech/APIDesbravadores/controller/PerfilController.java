package school.sptech.APIDesbravadores.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.APIDesbravadores.domain.Perfil;
import school.sptech.APIDesbravadores.service.PerfilService;

import java.util.List;

@RestController
@RequestMapping("/perfil")
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping
    public ResponseEntity<List<Perfil>> listarPerfil(){
        List<Perfil> perfis = perfilService.listarPerfis();
        if (perfis.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(perfis);
    }
}
