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
import school.sptech.APIDesbravadores.domain.Perfil;
import school.sptech.APIDesbravadores.domain.Usuario;
import school.sptech.APIDesbravadores.dto.UsuarioCriacaoDto;
import school.sptech.APIDesbravadores.dto.UsuarioLoginDto;
import school.sptech.APIDesbravadores.dto.UsuarioTokenDto;
import school.sptech.APIDesbravadores.exception.ClubeNãoEncontradoException;
import school.sptech.APIDesbravadores.exception.EmailJaCadastradoException;
import school.sptech.APIDesbravadores.exception.PerfilNaoEncontradoException;
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

    public Usuario cadastarUsuario(UsuarioCriacaoDto request){
        System.out.println("[DEBUG] - Iniciando validações para cadastro de usuario. Arquivo: UsuarioService Função: cadastarUsuario()");
        Optional<Clube> clube = clubeRepository.findById(request.getIdClube());
        System.out.println("[DEBUG] - O clube existe:" + (clube.isEmpty()?"Não":"Sim"));
        if (clube.isEmpty()){
            System.out.println("[ERROR] - O clube não existe, lançando ClubeNãoEncontradoException() ");
            throw new ClubeNãoEncontradoException();
        }
        System.out.println("[DEBUG] - Validando duplicidade de Email, o email " + (usuarioRepository.findByEmail(request.getEmail()).isEmpty()?"não está duplicado":"está duplicado"));
        if (!usuarioRepository.findByEmail(request.getEmail()).isEmpty()){
            System.out.println("[ERROR] - O usuário está informou um e-mail duplicado, lançado EmailJaCadastradoException()");
            throw new EmailJaCadastradoException();
        }
        System.out.println("[DEBUG] - Validando o perfil de usuário, o Perfil " + (perfilRepository.findById(request.getIdPerfil()).isEmpty()?"não existe":"existe") );
        Optional<Perfil> perfil = perfilRepository.findById(request.getIdPerfil());
        if (perfil.isEmpty()){
            System.out.println("[ERROR] - O perfil não foi encontrado, lançado PerfilNaoEncontradoException()");
            throw new PerfilNaoEncontradoException();
        }
        Usuario usuario = UsuarioMapper.toEntity(request);
        String senhaCriptografada = passwordEncoder.encode(request.getSenha());
        usuario.setSenha(senhaCriptografada);
        usuario.setClube(clube.get());
        usuario.setPerfil(perfil.get());
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
                usuarioAutenticado.getPerfil().getNome(),
                token
        );
    }
}
