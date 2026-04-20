package school.sptech.APIDesbravadores.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.domain.Usuario;
import school.sptech.APIDesbravadores.dto.UsuarioCriacaoDto;
import school.sptech.APIDesbravadores.dto.UsuarioLoginDto;
import school.sptech.APIDesbravadores.dto.UsuarioSessaoDto;
import school.sptech.APIDesbravadores.dto.UsuarioTokenDto;
import school.sptech.APIDesbravadores.service.UsuarioService;

import java.time.Duration;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Usuario> criarUsuario(@RequestBody @Valid UsuarioCriacaoDto request){
        return ResponseEntity.status(201).body(usuarioService.cadastarUsuario(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioSessaoDto> login(
            @RequestBody UsuarioLoginDto usuarioLoginDto,
            HttpServletResponse response) { // Precisamos do Response para colar o Cookie!

        System.out.println("Comecei o login");

        // Manda o email e senha pra Service e recebe o Token de volta
        UsuarioTokenDto autenticado = this.usuarioService.autenticar(usuarioLoginDto);

        System.out.println(autenticado);

        // AQUI ESTÁ O SEGREDO DO COOKIE: Amarra a pulseira no pulso do cliente!
        ResponseCookie cookie = ResponseCookie.from("authToken", autenticado.getToken())
                .httpOnly(true) // JavaScript do front-end NÃO consegue roubar isso! (Segurança Anti-Hacker)
                .secure(false)  // false no localhost. Quando for pra nuvem com HTTPS, muda pra true.
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofHours(2)) // Cookie dura 2 horas
                .build();

        System.out.println(cookie);

        // Cola o Cookie no Cabeçalho da resposta
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Cria a resposta pro Front-end (Nome e Email), MAS SEM EXPOR O TOKEN NO BODY!
        UsuarioSessaoDto sessao = new UsuarioSessaoDto(
                autenticado.getIdUsuario(),
                autenticado.getNome(),
                autenticado.getEmail(),
                autenticado.getTipoConta()
        );

        return ResponseEntity.ok(sessao);
    }

    @PostMapping("/logoff")
    public ResponseEntity<Void> logoff(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("authToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(0) // Isso aqui é o que diz pro navegador: "Destrua esse cookie AGORA"
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/painel-diretoria")
    @PreAuthorize("hasRole('DIRETOR')") // O Spring Security vai olhar a pulseira antes de rodar essa linha
    public ResponseEntity<String> painelExclusivo() {
        return ResponseEntity.ok("Sucesso! Você entrou no camarote dos Diretores.");
    }
}
