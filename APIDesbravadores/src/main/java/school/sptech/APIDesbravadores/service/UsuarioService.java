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
import school.sptech.APIDesbravadores.mapper.UsuarioMapper;
import school.sptech.APIDesbravadores.repository.ClubeRepository;
import school.sptech.APIDesbravadores.repository.PerfilRepository;
import school.sptech.APIDesbravadores.repository.UsuarioRepository;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final GerenciadorTokenJwt gerenciadorTokenJwt;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ClubeRepository clubeRepository;
    private final PerfilRepository perfilRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, GerenciadorTokenJwt gerenciadorTokenJwt, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, ClubeRepository clubeRepository, PerfilRepository perfilRepository) {
        this.usuarioRepository = usuarioRepository;
        this.gerenciadorTokenJwt = gerenciadorTokenJwt;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.clubeRepository = clubeRepository;
        this.perfilRepository = perfilRepository;
    }

    @Transactional
    public Usuario cadastrarUsuario(UsuarioCriacaoDto request){
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
        return dto;
    }

    @Transactional
    public UsuarioResponseDto atualizarPerfil(String emailLogado, UsuarioPerfilUpdateDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
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
}
