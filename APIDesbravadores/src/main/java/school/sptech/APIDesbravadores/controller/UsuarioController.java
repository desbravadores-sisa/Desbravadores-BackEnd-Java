package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.dto.*;
import school.sptech.APIDesbravadores.mapper.UsuarioMapper;
import school.sptech.APIDesbravadores.service.UsuarioService;

import java.time.Duration;
import java.util.List;

@Tag(name = "Usuários", description = "Endpoints para gerenciamento e cadastro de usuários")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Cadastrar usuário via Convite",
            description = "Cria uma nova conta de usuário utilizando um token de convite válido. O e-mail deve corresponder ao do convite.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso",
                    content = @Content(schema = @Schema(implementation = UsuarioResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos (JSON inválido)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Token de convite não encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito: E-mail já cadastrado ou Convite já utilizado/revogado",
                    content = @Content),
            @ApiResponse(responseCode = "410", description = "Gone: O convite está expirado ou aceito", content = @Content),
            @ApiResponse(responseCode = "422", description = "Regra de Negócio: Convite expirado ou e-mail divergente do convite",
                    content = @Content)
    })
    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDto> criarUsuario(@RequestBody @Valid UsuarioCriacaoDto request){
        System.out.println("[DEBUG] - Iniciando Cadastro da API, Arquivo UsuarioController Function: criarUsuario");
        System.out.println("[DEBUG] - Parametro recebido: \n" + request + "\n Arquivo UsuarioController Function: criarUsuario");
        return ResponseEntity.status(201).body(UsuarioMapper.toResponse(usuarioService.cadastrarUsuario(request)));
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

   @GetMapping("")
   @PreAuthorize("hasRole('DIRETORIA')")
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuarios(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado){
        return ResponseEntity.ok(UsuarioMapper.toResponse(usuarioService.listarUsuario(usuariologado.getIdClube())));
   }

    @DeleteMapping("")
    @PreAuthorize("hasRole('DIRETORIA')")
    public ResponseEntity<Void> inativarUsuario(@AuthenticationPrincipal UsuarioDetalhesDto usuariologado, @RequestParam Integer idUsuario){
        usuarioService.inativarUsuario(usuariologado.getIdClube(),idUsuario,usuariologado.getIdUsuario());
        return ResponseEntity.noContent().build();
    }
}
