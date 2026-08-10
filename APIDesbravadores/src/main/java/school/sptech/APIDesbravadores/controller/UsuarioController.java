package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.dto.UsuarioCriacaoDto;
import school.sptech.APIDesbravadores.dto.UsuarioLoginDto;
import school.sptech.APIDesbravadores.dto.UsuarioResponseDto;
import school.sptech.APIDesbravadores.dto.UsuarioSessaoDto;
import school.sptech.APIDesbravadores.dto.UsuarioTokenDto;
import school.sptech.APIDesbravadores.mapper.UsuarioMapper;
import school.sptech.APIDesbravadores.service.UsuarioService;

import java.time.Duration;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "Endpoints de cadastro, autenticação e sessão de usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;

    // O cookie precisa morrer junto com o token, senão o usuário parece logado e toma 401.
    private final long jwtValidity;

    public UsuarioController(UsuarioService usuarioService, @Value("${jwt.validity}") long jwtValidity) {
        this.usuarioService = usuarioService;
        this.jwtValidity = jwtValidity;
    }

    @PostMapping("/cadastro")
    @Operation(summary = "Cadastrar um novo usuário", description = "Cria um usuário com a senha criptografada em BCrypt")
    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Clube não encontrado")
    @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    public ResponseEntity<UsuarioResponseDto> criarUsuario(@RequestBody @Valid UsuarioCriacaoDto request){
        return ResponseEntity.status(201).body(UsuarioMapper.toResponse(usuarioService.cadastrarUsuario(request)));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar um usuário",
            description = "Valida as credenciais e devolve o token JWT no cookie httpOnly 'authToken'. O corpo da resposta traz apenas os dados da sessão, sem o token.")
    @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso")
    @ApiResponse(responseCode = "401", description = "E-mail ou senha inválidos")
    public ResponseEntity<UsuarioSessaoDto> login(
            @RequestBody UsuarioLoginDto usuarioLoginDto,
            HttpServletResponse response) { // Precisamos do Response para colar o Cookie!

        // Manda o email e senha pra Service e recebe o Token de volta
        UsuarioTokenDto autenticado = this.usuarioService.autenticar(usuarioLoginDto);

        // AQUI ESTÁ O SEGREDO DO COOKIE: Amarra a pulseira no pulso do cliente!
        ResponseCookie cookie = ResponseCookie.from("authToken", autenticado.getToken())
                .httpOnly(true) // JavaScript do front-end NÃO consegue roubar isso! (Segurança Anti-Hacker)
                .secure(false)  // false no localhost. Quando for pra nuvem com HTTPS, muda pra true.
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofSeconds(jwtValidity)) // Mesma validade do token
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
    @Operation(summary = "Encerrar a sessão do usuário", description = "Expira o cookie 'authToken' no navegador, invalidando a sessão")
    @ApiResponse(responseCode = "200", description = "Sessão encerrada com sucesso")
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
    @Operation(summary = "Acessar o painel da diretoria", description = "Endpoint restrito para validar a permissão de DIRETOR")
    @ApiResponse(responseCode = "200", description = "Acesso liberado")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "403", description = "Usuário não possui a permissão DIRETOR")
    public ResponseEntity<String> painelExclusivo() {
        return ResponseEntity.ok("Sucesso! Você entrou no camarote dos Diretores.");
    }
}
