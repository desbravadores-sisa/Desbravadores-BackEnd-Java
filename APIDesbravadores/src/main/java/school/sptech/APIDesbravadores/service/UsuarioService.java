package school.sptech.APIDesbravadores.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.APIDesbravadores.config.GerenciadorTokenJwt;
import school.sptech.APIDesbravadores.domain.Convite;
import school.sptech.APIDesbravadores.domain.Usuario;
import school.sptech.APIDesbravadores.dto.UsuarioCriacaoDto;
import school.sptech.APIDesbravadores.dto.UsuarioLoginDto;
import school.sptech.APIDesbravadores.dto.UsuarioTokenDto;
import school.sptech.APIDesbravadores.exception.*;
import school.sptech.APIDesbravadores.mapper.UsuarioMapper;
import school.sptech.APIDesbravadores.repository.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final GerenciadorTokenJwt gerenciadorTokenJwt;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ClubeRepository clubeRepository;
    private final PerfilRepository perfilRepository;
    private final UnidadeRepository unidadeRepository;
    private final ConviteRepository conviteRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, GerenciadorTokenJwt gerenciadorTokenJwt, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, ClubeRepository clubeRepository, PerfilRepository perfilRepository, UnidadeRepository unidadeRepository, ConviteRepository conviteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.gerenciadorTokenJwt = gerenciadorTokenJwt;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.clubeRepository = clubeRepository;
        this.perfilRepository = perfilRepository;
        this.unidadeRepository = unidadeRepository;
        this.conviteRepository = conviteRepository;
    }

    public List<Usuario> listarUsuario(Integer idClube){
        if (idClube == null){
            throw new RegraNegocioException("");
        }
        validacaoClube(idClube);
        List<Usuario> usuarios =  usuarioRepository.findByClubeIdAndAtivo(idClube,true);
        return usuarios;
    }

    public void validacaoClube(Integer idClube){
        if (!clubeRepository.existsById(idClube)){
            throw new ClubeNãoEncontradoException();
        }
    }

    public void validacaoUnidade(Integer idUnidade){
        if (!unidadeRepository.existsById(idUnidade)){
            throw new UnidadeNãoEncontradaException();
        }
    }

    public void validacaoPerfil(Integer idPerfil){
        if (!perfilRepository.existsById(idPerfil)){
            throw new PerfilNaoEncontradoException();
        }
    }

    public Convite validarConvite(String token){
        Convite convite = conviteRepository.findByToken(token).orElseThrow(ConviteNãoEncontradoException::new);
        String status = convite.getStatusConvite();

        validacaoClube(convite.getClube().getId());
        if (!convite.getPerfil().getNome().equalsIgnoreCase("DIRETORIA")){
            validacaoUnidade(convite.getUnidade().getId());
        }

        if (convite.getDataExpiracao().isBefore(LocalDateTime.now()) && status.equalsIgnoreCase("pendente")){
            convite.setStatusConvite("EXPIRADO");
            status = "EXPIRADO";
            conviteRepository.save(convite);
        }
        if (status.equalsIgnoreCase("EXPIRADO")){
            throw new ConviteExpiradoException("Este convite já consta como expirado no sistema.");
        }
        if (status.equalsIgnoreCase("REVOGADO") || status.equalsIgnoreCase("ACEITO")){
            throw new ConviteConflitoEstadoException("Este convite já foi utilizado ou revogado pela diretoria.");
        }

        return convite;
    }

    public Usuario cadastrarUsuario(UsuarioCriacaoDto request){
        Convite convite = validarConvite(request.getToken());

        // Validação E-mail
        if (!convite.getEmail().equals(request.getEmail())){
            throw new RegraNegocioException("O e-mail informado no cadastro não corresponde ao e-mail autorizado neste convite.");
        }
        System.out.println("[DEBUG] - Validando duplicidade de Email, o email " + (usuarioRepository.findByEmail(request.getEmail()).isEmpty()?"não está duplicado":"está duplicado"));
        if (!usuarioRepository.findByEmail(request.getEmail()).isEmpty()){
            System.out.println("[ERROR] - O usuário está informou um e-mail duplicado, lançado EmailJaCadastradoException()");
            throw new EmailJaCadastradoException();
        }

        // Validação Perfil
        validacaoPerfil(convite.getPerfil().getId());

        Usuario usuario = UsuarioMapper.toEntity(request);
        String senhaCriptografada = passwordEncoder.encode(request.getSenha());
        usuario.setSenha(senhaCriptografada);
        usuario.setClube(convite.getClube());
        usuario.setPerfil(convite.getPerfil());
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
