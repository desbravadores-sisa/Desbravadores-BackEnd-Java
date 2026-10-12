package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.dto.NotificacaoResponseDto;
import school.sptech.APIDesbravadores.dto.UsuarioDetalhesDto;
import school.sptech.APIDesbravadores.service.NotificacaoService;

import java.util.List;

@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping
    @Operation(summary = "Listar notificações do usuário logado")
    public ResponseEntity<List<NotificacaoResponseDto>> listar(@AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        if (usuarioLogado == null) return ResponseEntity.status(401).build();
        List<NotificacaoResponseDto> lista = notificacaoService.listar(usuarioLogado.getEmail());
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @PatchMapping("/{id}/lida")
    @Operation(summary = "Marcar uma notificação como lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Integer id, @AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        if (usuarioLogado == null) return ResponseEntity.status(401).build();
        notificacaoService.marcarComoLida(id, usuarioLogado.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/lidas")
    @Operation(summary = "Marcar todas as notificações do usuário como lidas")
    public ResponseEntity<Void> marcarTodas(@AuthenticationPrincipal UsuarioDetalhesDto usuarioLogado) {
        if (usuarioLogado == null) return ResponseEntity.status(401).build();
        notificacaoService.marcarTodasComoLidas(usuarioLogado.getEmail());
        return ResponseEntity.noContent().build();
    }
}
