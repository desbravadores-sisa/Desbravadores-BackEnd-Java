package school.sptech.APIDesbravadores.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import school.sptech.APIDesbravadores.config.GerenciadorTokenJwt;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.Convite;
import school.sptech.APIDesbravadores.domain.Perfil;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.domain.Usuario;
import school.sptech.APIDesbravadores.dto.UsuarioCriacaoDto;
import school.sptech.APIDesbravadores.exception.EmailJaCadastradoException;
import school.sptech.APIDesbravadores.repository.ClubeRepository;
import school.sptech.APIDesbravadores.repository.ConviteRepository;
import school.sptech.APIDesbravadores.repository.PerfilRepository;
import school.sptech.APIDesbravadores.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private GerenciadorTokenJwt gerenciadorTokenJwt;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ClubeRepository clubeRepository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private ConviteRepository conviteRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void cadastrarUsuarioDeveReativarUsuarioInativoNoCadastroDireto() {
        UsuarioCriacaoDto request = criarRequestDireto();
        Clube clube = new Clube();
        clube.setId(1);
        Perfil perfil = new Perfil();
        perfil.setNome("DIRETOR");
        Usuario existente = new Usuario();
        existente.setId(15);
        existente.setAtivo(false);

        when(clubeRepository.findById(1)).thenReturn(Optional.of(clube));
        when(perfilRepository.findByNomeIgnoreCase("DIRETOR")).thenReturn(Optional.of(perfil));
        when(usuarioRepository.findByEmail("ana@email.com")).thenReturn(Optional.of(existente));
        when(passwordEncoder.encode("senhaNova123")).thenReturn("senha-criptografada");
        when(usuarioRepository.save(existente)).thenReturn(existente);

        Usuario resultado = usuarioService.cadastrarUsuario(request);

        assertSame(existente, resultado);
        assertEquals(15, resultado.getId());
        assertTrue(resultado.getAtivo());
        assertEquals("Ana", resultado.getNome());
        assertEquals("senha-criptografada", resultado.getSenha());
        assertSame(clube, resultado.getClube());
        assertSame(perfil, resultado.getPerfil());
        verify(usuarioRepository).save(existente);
    }

    @Test
    void cadastrarUsuarioDeveReativarUsuarioInativoPorConvite() {
        UsuarioCriacaoDto request = new UsuarioCriacaoDto();
        request.setNome("Ana Convite");
        request.setEmail("ana@email.com");
        request.setSenha("senhaNova123");
        request.setToken("token-convite");

        Clube clube = new Clube();
        Perfil perfil = new Perfil();
        Unidade unidade = new Unidade();
        Convite convite = new Convite();
        convite.setEmail("ana@email.com");
        convite.setToken("token-convite");
        convite.setStatusConvite("PENDENTE");
        convite.setDataExpiracao(LocalDateTime.now().plusDays(1));
        convite.setClube(clube);
        convite.setPerfil(perfil);
        convite.setUnidade(unidade);
        Usuario existente = new Usuario();
        existente.setId(21);
        existente.setAtivo(false);

        when(conviteRepository.findByToken("token-convite")).thenReturn(Optional.of(convite));
        when(usuarioRepository.findByEmail("ana@email.com")).thenReturn(Optional.of(existente));
        when(passwordEncoder.encode("senhaNova123")).thenReturn("senha-criptografada");
        when(usuarioRepository.save(existente)).thenReturn(existente);

        Usuario resultado = usuarioService.cadastrarUsuario(request);

        assertSame(existente, resultado);
        assertEquals(21, resultado.getId());
        assertTrue(resultado.getAtivo());
        assertEquals("Ana Convite", resultado.getNome());
        assertSame(clube, resultado.getClube());
        assertSame(perfil, resultado.getPerfil());
        assertSame(unidade, resultado.getUnidade());
        assertEquals("ACEITO", convite.getStatusConvite());
        verify(usuarioRepository).save(existente);
        verify(conviteRepository).save(convite);
    }

    @Test
    void cadastrarUsuarioDeveRejeitarEmailJaAssociadoAUsuarioAtivo() {
        UsuarioCriacaoDto request = criarRequestDireto();
        Clube clube = new Clube();
        Perfil perfil = new Perfil();
        Usuario existente = new Usuario();
        existente.setAtivo(true);

        when(clubeRepository.findById(1)).thenReturn(Optional.of(clube));
        when(perfilRepository.findByNomeIgnoreCase("DIRETOR")).thenReturn(Optional.of(perfil));
        when(usuarioRepository.findByEmail("ana@email.com")).thenReturn(Optional.of(existente));

        assertThrows(EmailJaCadastradoException.class, () -> usuarioService.cadastrarUsuario(request));
    }

    private UsuarioCriacaoDto criarRequestDireto() {
        UsuarioCriacaoDto request = new UsuarioCriacaoDto();
        request.setNome("Ana");
        request.setEmail("ana@email.com");
        request.setSenha("senhaNova123");
        request.setTipoConta("DIRETOR");
        request.setIdClube(1);
        return request;
    }
}