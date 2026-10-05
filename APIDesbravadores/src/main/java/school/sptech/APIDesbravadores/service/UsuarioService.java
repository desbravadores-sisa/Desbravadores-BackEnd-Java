package school.sptech.APIDesbravadores.service;

import jakarta.persistence.criteria.CriteriaBuilder;
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
import school.sptech.APIDesbravadores.dto.*;
import school.sptech.APIDesbravadores.exception.*;
import school.sptech.APIDesbravadores.mapper.UsuarioMapper;
import school.sptech.APIDesbravadores.repository.*;

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
        // Validação Perfil
        validacaoPerfil(convite.getPerfil().getId());

        Optional<Usuario> usuarioInativo = usuarioRepository.findByEmail(request.getEmail());
        Usuario usuario;
        if (usuarioInativo.isPresent()){
            usuario  = usuarioInativo.get();

            if (usuario.getAtivo()){
                throw new EmailJaCadastradoException();
            }

            if (!usuario.getClube().getId().equals(convite.getClube().getId())){
                throw new RegraNegocioException("Há um usuário ativo com o e-mail em outra unidade");
            }

            usuario.setAtivo(true);
            usuario.setNome(request.getNome());
        } else {
            usuario = UsuarioMapper.toEntity(request);
        }

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

    public void inativarUsuario(Integer idClube, Integer idUsuario, Integer idUsuarioLogado){

        if (idUsuario.equals(idUsuarioLogado)){
            throw new AcessoNegadoException("O usuário não pode inativar do sistema");
        }
        validacaoClube(idClube);
        Usuario usuarioLogado = usuarioRepository.findById(idUsuarioLogado).orElseThrow(() -> new UsuarioNaoEncontradoException());
        Usuario usuarioExclusao = usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontradoException());
        if (!usuarioLogado.getClube().getId().equals(idClube)){
            throw new AcessoNegadoException("O usuário não tem acesso, para inativar usuários desse clube");
        }

        if (!usuarioExclusao.getClube().getId().equals(idClube)) {
            throw new AcessoNegadoException("O usuário a ser inativado não pertence a este clube.");
        }

        if (usuarioExclusao.getPerfil().getNome().equalsIgnoreCase("DIRETORIA")){
            List<Usuario> usuarioAtivos = usuarioRepository.findByClubeIdAndAtivoAndPerfilNome(idClube,true,"DIRETORIA");
            if (usuarioAtivos.size() < 2){
                throw new RegraNegocioException("Não é possível inativar o último usuário com cargo de diretoria neste clube.");
            }
        }

        usuarioExclusao.setAtivo(false);
        usuarioRepository.save(usuarioExclusao);
    }

    public Usuario buscarDadosUsuario(Integer idUsuario){
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(UsuarioNaoEncontradoException::new);
        return usuario;
    }

    public Usuario alterarNomeUsuario(Integer idUsuario, String nome){
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(UsuarioNaoEncontradoException::new);
        usuario.setNome(nome);
        usuarioRepository.save(usuario);
        return usuario;
    }

    public Usuario alterarSenhaUsuario(Integer idUsuario, UsuarioAlteracaoSenhaDto dto){
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(UsuarioNaoEncontradoException::new);
        if (passwordEncoder.encode(dto.getSenhaAtual()).equals(usuario.getSenha())){
            throw new RegraNegocioException("A senha atual");
        }
        String senhaCriptografada = passwordEncoder.encode(dto.getSenhaNova());
        usuario.setSenha(senhaCriptografada);
        usuarioRepository.save(usuario);
        return usuario;
    }
}
