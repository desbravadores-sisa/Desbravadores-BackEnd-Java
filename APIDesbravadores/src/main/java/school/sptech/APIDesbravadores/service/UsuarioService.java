package school.sptech.APIDesbravadores.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.APIDesbravadores.config.GerenciadorTokenJwt;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.Convite;
import school.sptech.APIDesbravadores.domain.Perfil;
import school.sptech.APIDesbravadores.domain.Usuario;
import school.sptech.APIDesbravadores.dto.UsuarioCriacaoDto;
import school.sptech.APIDesbravadores.dto.UsuarioLoginDto;
import school.sptech.APIDesbravadores.dto.UsuarioPerfilUpdateDto;
import school.sptech.APIDesbravadores.dto.UsuarioResponseDto;
import school.sptech.APIDesbravadores.dto.UsuarioSenhaUpdateDto;
import school.sptech.APIDesbravadores.dto.UsuarioTokenDto;
import school.sptech.APIDesbravadores.exception.ClubeNãoEncontradoException;
import school.sptech.APIDesbravadores.exception.EmailJaCadastradoException;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;
import school.sptech.APIDesbravadores.mapper.UsuarioMapper;
import school.sptech.APIDesbravadores.repository.ClubeRepository;
import school.sptech.APIDesbravadores.repository.ConviteRepository;
import school.sptech.APIDesbravadores.repository.PerfilRepository;
import school.sptech.APIDesbravadores.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final GerenciadorTokenJwt gerenciadorTokenJwt;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ClubeRepository clubeRepository;
    private final PerfilRepository perfilRepository;
    private final ConviteRepository conviteRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, GerenciadorTokenJwt gerenciadorTokenJwt, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, ClubeRepository clubeRepository, PerfilRepository perfilRepository, ConviteRepository conviteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.gerenciadorTokenJwt = gerenciadorTokenJwt;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.clubeRepository = clubeRepository;
        this.perfilRepository = perfilRepository;
        this.conviteRepository = conviteRepository;
    }

    @Transactional
    public Usuario cadastrarUsuario(UsuarioCriacaoDto request){
        if (request.getToken() != null && !request.getToken().isBlank()) {
            return cadastrarPorConvite(request);
        }
        if (request.getIdClube() == null || request.getTipoConta() == null || request.getTipoConta().isBlank()) {
            throw new RequisicaoInvalidaException("Informe o token do convite ou tipoConta e idClube");
        }
        Optional<Clube> clube = clubeRepository.findById(request.getIdClube());
        if (clube.isEmpty()){
            throw new ClubeNãoEncontradoException();
        }
        Perfil perfil = perfilRepository.findByNomeIgnoreCase(request.getTipoConta())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Perfil não encontrado: " + request.getTipoConta()));
        if (!usuarioRepository.findByEmail(request.getEmail()).isEmpty()){
            throw new EmailJaCadastradoException();
        }
        Usuario usuario = UsuarioMapper.toEntity(request, perfil);
        String senhaCriptografada = passwordEncoder.encode(request.getSenha());
        usuario.setSenha(senhaCriptografada);
        usuario.setClube(clube.get());
        usuarioRepository.save(usuario);
        return usuario;
    }

    /**
     * Fluxo da tela /cadastro?token=...: perfil, clube e unidade vêm do convite.
     * Códigos esperados pelo front: 404 convite inexistente, 410 expirado/usado,
     * 422 e-mail diferente do convite, 409 e-mail já cadastrado.
     */
    private Usuario cadastrarPorConvite(UsuarioCriacaoDto request) {
        Convite convite = conviteRepository.findByToken(request.getToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convite não encontrado"));

        if ("PENDENTE".equalsIgnoreCase(convite.getStatusConvite())
                && convite.getDataExpiracao() != null
                && convite.getDataExpiracao().isBefore(LocalDateTime.now())) {
            convite.setStatusConvite("EXPIRADO");
            conviteRepository.save(convite);
        }
        if (!"PENDENTE".equalsIgnoreCase(convite.getStatusConvite())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Convite expirado ou já utilizado");
        }
        if (!convite.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "E-mail não corresponde ao convite");
        }
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailJaCadastradoException();
        }

        Usuario usuario = UsuarioMapper.toEntity(request, convite.getPerfil());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setClube(convite.getClube());
        usuario.setUnidade(convite.getUnidade());
        usuarioRepository.save(usuario);

        convite.setStatusConvite("ACEITO");
        conviteRepository.save(convite);
        return usuario;
    }

    public UsuarioTokenDto autenticar(UsuarioLoginDto loginDto) {
        final UsernamePasswordAuthenticationToken credentials =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getSenha());
        final Authentication authentication = this.authenticationManager.authenticate(credentials);
        Usuario usuarioAutenticado = usuarioRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não cadastrado", null));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = gerenciadorTokenJwt.generateToken(authentication);
        return new UsuarioTokenDto(
                usuarioAutenticado.getId(),
                usuarioAutenticado.getNome(),
                usuarioAutenticado.getEmail(),
                usuarioAutenticado.getPerfil() != null ? usuarioAutenticado.getPerfil().getNome() : null,
                token
        );
    }

    public UsuarioResponseDto buscarPerfil(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));
        UsuarioResponseDto dto = new UsuarioResponseDto();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setTipoConta(usuario.getPerfil() != null ? usuario.getPerfil().getNome() : null);
        dto.setIdClube(usuario.getClube() != null ? usuario.getClube().getId() : null);
        dto.setIdUnidade(usuario.getUnidade() != null ? usuario.getUnidade().getId() : null);
        dto.setTelefone(usuario.getTelefone());
        dto.setFotoUrl(usuario.getFotoUrl());
        return dto;
    }

    public List<UsuarioResponseDto> buscarUsuarios(Integer idClube){
        if (idClube == null || !clubeRepository.existsById(idClube)){
            throw new ClubeNãoEncontradoException();
        }
        List<Usuario> usuarios = usuarioRepository.findByClubeId(idClube);
        return usuarios.stream().map(UsuarioMapper::toResponse).toList();
    }

    @Transactional
    public UsuarioResponseDto atualizarPerfil(String emailLogado, UsuarioPerfilUpdateDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));
        if (!usuario.getEmail().equalsIgnoreCase(dto.getEmail())
                && usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailJaCadastradoException();
        }
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefone(dto.getTelefone());
        usuarioRepository.save(usuario);
        return buscarPerfil(dto.getEmail());
    }

    @Transactional
    public void alterarSenha(String emailLogado, UsuarioSenhaUpdateDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));
        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha atual incorreta");
        }
        usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        usuarioRepository.save(usuario);
    }

    /** Troca a foto de perfil (arquivo na pasta de uploads; o banco guarda só o caminho). */
    @Transactional
    public UsuarioResponseDto atualizarFoto(String emailLogado, org.springframework.web.multipart.MultipartFile arquivo,
                                            ArmazenamentoArquivoService armazenamento) {
        Usuario usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));
        String anterior = usuario.getFotoUrl();
        usuario.setFotoUrl(armazenamento.salvar(arquivo, true));
        usuarioRepository.save(usuario);
        armazenamento.remover(anterior);
        return buscarPerfil(emailLogado);
    }

    @Transactional
    public UsuarioResponseDto removerFoto(String emailLogado, ArmazenamentoArquivoService armazenamento) {
        Usuario usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));
        armazenamento.remover(usuario.getFotoUrl());
        usuario.setFotoUrl(null);
        usuarioRepository.save(usuario);
        return buscarPerfil(emailLogado);
    }
}
