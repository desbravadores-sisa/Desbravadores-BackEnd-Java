package school.sptech.APIDesbravadores.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.domain.Convite;
import school.sptech.APIDesbravadores.dto.*;
import school.sptech.APIDesbravadores.mapper.ConviteMapper;
import school.sptech.APIDesbravadores.service.ConviteService;

import java.util.List;

@RestController
@RequestMapping("/convites")
public class ConviteController {

    private final ConviteService conviteService;

    public ConviteController(ConviteService conviteService) {
        this.conviteService = conviteService;
    }

    @GetMapping("")
    @PreAuthorize("hasRole('DIRETOR')")
    public ResponseEntity<List<ConviteResponseDto>> listarUnidades(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado){
        Integer idClube = usuariologado.getIdClube();
        System.out.println("IdClube na Controller" + idClube);
        return ResponseEntity.ok(conviteService.listarConvites(idClube).stream().map((item) -> ConviteMapper.toResponse(item)).toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('DIRETOR')")
    public ResponseEntity<ConviteResponseDto> criarConvite(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado, @RequestBody @Valid ConviteRequestDto request){
        Integer idClube = usuariologado.getIdClube();
        return ResponseEntity.status(201).body(ConviteMapper.toResponse(conviteService.criarConvite(request)));
    }

    @GetMapping("/validar")
    public ResponseEntity<Boolean> validarConvite(Integer idConvite){
        return null; // ResponseEntity.ok(conviteService.validarConvite(idConvite));
    }

    @GetMapping("/teste/{token}")
    public ResponseEntity<Void> teste(@PathVariable String token){
        conviteService.validarConvite(token);
        return ResponseEntity.ok().build();
    }

}
