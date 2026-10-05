package school.sptech.APIDesbravadores.controller;

import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.dto.NotificacaoResponseDto;
import school.sptech.APIDesbravadores.service.NotificacaoService;

@RestController
@RequestMapping("/api/notifications")
public class NotificacaoController {
    private final NotificacaoService service;
    public NotificacaoController(NotificacaoService service) { this.service = service; }

    @GetMapping
    public Page<NotificacaoResponseDto> listar(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) { return service.listar(page, size); }

    @GetMapping("/unread-count")
    public Map<String, Long> contar() { return Map.of("count", service.naoLidas()); }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> ler(@PathVariable Integer id) {
        service.ler(id); return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> lerTodas() { service.lerTodas(); return ResponseEntity.noContent().build(); }
}
