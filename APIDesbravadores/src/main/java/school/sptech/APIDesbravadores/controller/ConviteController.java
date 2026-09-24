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
    @PreAuthorize("hasRole('DIRETORIA')")
    public ResponseEntity<List<ConviteResponseDto>> listarUnidades(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado, @RequestParam(required = false) String statusConvite){
        Integer idClube = usuariologado.getIdClube();
        List<ConviteResponseDto> response = conviteService.listarConvites(idClube,statusConvite);
        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('DIRETORIA')")
    public ResponseEntity<ConviteResponseDto> criarConvite(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado, @RequestBody @Valid ConviteCriacaoRequestDto request){
        Integer idClube = usuariologado.getIdClube();
        return ResponseEntity.status(201).body(ConviteMapper.toResponse(conviteService.criarConvite(request,idClube)));
    }



}
