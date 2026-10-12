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
    public ResponseEntity<List<ConviteResponseDto>> listarUnidades(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado){
        Integer idClube = usuariologado.getIdClube();
        System.out.println("IdClube na Controller" + idClube);
        return ResponseEntity.ok(conviteService.listarConvites(idClube).stream().map((item) -> ConviteMapper.toResponse(item)).toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('DIRETORIA')")
    public ResponseEntity<ConviteResponseDto> criarConvite(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado, @RequestBody @Valid ConviteCriacaoDto request){
        Integer idClube = usuariologado.getIdClube();
        Convite convite = conviteService.criarConvite(request, idClube);
        ConviteResponseDto response = ConviteMapper.toResponse(convite);
        response.setToken(convite.getToken());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/validar")
    public ResponseEntity<Boolean> validarConvite(Integer idConvite){
        return ResponseEntity.ok(conviteService.validarConvite(idConvite));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DIRETORIA')")
    public ResponseEntity<ConviteResponseDto> atualizarConvite(@RequestBody @Valid ConviteUpdateDto updateDto, @PathVariable Integer id){
        return ResponseEntity.ok(ConviteMapper.toResponse(conviteService.atualizarConvite(updateDto, id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DIRETORIA')")
    public ResponseEntity<Void> deletarConvite(@PathVariable Integer id){
        conviteService.deletarConvite(id);
        return ResponseEntity.noContent().build();
    }

}
