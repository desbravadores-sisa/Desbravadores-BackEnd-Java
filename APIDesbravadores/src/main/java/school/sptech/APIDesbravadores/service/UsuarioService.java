package school.sptech.APIDesbravadores.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.APIDesbravadores.config.GerenciadorTokenJwt;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.Usuario;
import school.sptech.APIDesbravadores.dto.UsuarioCriacaoDto;
import school.sptech.APIDesbravadores.dto.UsuarioLoginDto;
import school.sptech.APIDesbravadores.dto.UsuarioTokenDto;
import school.sptech.APIDesbravadores.exception.ClubeNãoEncontradoException;
import school.sptech.APIDesbravadores.exception.EmailJaCadastradoException;
import school.sptech.APIDesbravadores.mapper.UsuarioMapper;
import school.sptech.APIDesbravadores.repository.ClubeRepository;
import school.sptech.APIDesbravadores.repository.UsuarioRepository;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final GerenciadorTokenJwt gerenciadorTokenJwt;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ClubeRepository clubeRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, GerenciadorTokenJwt gerenciadorTokenJwt, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, ClubeRepository clubeRepository) {
        this.usuarioRepository = usuarioRepository;
        this.gerenciadorTokenJwt = gerenciadorTokenJwt;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.clubeRepository = clubeRepository;
    }

    public Usuario cadastarUsuario(UsuarioCriacaoDto request){
        Optional<Clube> clube = clubeRepository.findById(request.getIdClube());
        System.out.println(clube.isEmpty());
        if (clube.isEmpty()){
            throw new ClubeNãoEncontradoException();
        }
        System.out.println(usuarioRepository.findByEmail(request.getEmail()).isEmpty());
        if (!usuarioRepository.findByEmail(request.getEmail()).isEmpty()){
            throw new EmailJaCadastradoException();
        }
        Usuario usuario = UsuarioMapper.toEntity(request);
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
                .orElseThrow(() -> new ResponseStatusException(404, "Usuário não cadastrado", null));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = gerenciadorTokenJwt.generateToken(authentication);
        return new UsuarioTokenDto(
                usuarioAutenticado.getId(),
                usuarioAutenticado.getNome(),
                usuarioAutenticado.getEmail(),
                usuarioAutenticado.getTipoConta(),
                token
        );
    }
}
