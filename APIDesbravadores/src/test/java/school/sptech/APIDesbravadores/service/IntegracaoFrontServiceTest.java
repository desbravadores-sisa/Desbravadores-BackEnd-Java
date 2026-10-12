package school.sptech.APIDesbravadores.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.APIDesbravadores.config.GerenciadorTokenJwt;
import school.sptech.APIDesbravadores.domain.*;
import school.sptech.APIDesbravadores.dto.*;
import school.sptech.APIDesbravadores.exception.EmailJaCadastradoException;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;
import school.sptech.APIDesbravadores.repository.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Cobre os contratos ajustados para a integração com o frontend
 * (consolidacao/sprint-final): payloads que o React realmente envia.
 */
class IntegracaoFrontServiceTest {

    // ---------- Tarefas: o front não envia fkUnidade ----------

    @Test
    void criarTarefaSemUnidadeDistribuiParaTodasAsUnidadesDoClube() {
        TarefaRepository tarefaRepo = mock(TarefaRepository.class);
        TarefaUnidadeRepository tuRepo = mock(TarefaUnidadeRepository.class);
        ClubeRepository clubeRepo = mock(ClubeRepository.class);
        UnidadeRepository unidadeRepo = mock(UnidadeRepository.class);
        CicloRepository cicloRepo = mock(CicloRepository.class);
        CadernoRepository cadernoRepo = mock(CadernoRepository.class);
        TarefaService service = new TarefaService(tarefaRepo, tuRepo, clubeRepo, unidadeRepo, cicloRepo, cadernoRepo);

        Clube clube = new Clube();
        clube.setId(1);
        Unidade u1 = new Unidade(); u1.setId(10);
        Unidade u2 = new Unidade(); u2.setId(11);
        when(clubeRepo.findById(1)).thenReturn(Optional.of(clube));
        when(unidadeRepo.findByClubeId(1)).thenReturn(List.of(u1, u2));
        when(cicloRepo.findByClubeIdAndAtivoTrue(1)).thenReturn(Optional.of(new Ciclo()));
        when(tarefaRepo.save(any(Tarefa.class))).thenAnswer(inv -> { Tarefa t = inv.getArgument(0); t.setId(99); return t; });

        TarefaCreateDto dto = new TarefaCreateDto();
        dto.setFkClube(1);
        dto.setTitulo("Acampamento");
        dto.setTipoTarefa("CLUBE");
        dto.setPontuacao(20);

        TarefaResponseDto resp = service.create(dto);

        ArgumentCaptor<TarefaUnidade> captor = ArgumentCaptor.forClass(TarefaUnidade.class);
        verify(tuRepo, times(2)).save(captor.capture());
        assertEquals(List.of(10, 11), captor.getAllValues().stream().map(tu -> tu.getUnidade().getId()).toList());
        assertTrue(captor.getAllValues().stream().allMatch(tu -> tu.getStatusKanban() == StatusKanban.A_FAZER));
        assertEquals(99, resp.getId());
    }

    @Test
    void criarTarefaSemClubeEhRejeitada() {
        TarefaService service = new TarefaService(mock(TarefaRepository.class), mock(TarefaUnidadeRepository.class),
                mock(ClubeRepository.class), mock(UnidadeRepository.class), mock(CicloRepository.class), mock(CadernoRepository.class));
        TarefaCreateDto dto = new TarefaCreateDto();
        dto.setTitulo("x");
        dto.setTipoTarefa("CLUBE");
        assertThrows(RequisicaoInvalidaException.class, () -> service.create(dto));
    }

    // ---------- Evidências: decisão da diretoria vira status_kanban ----------

    private Evidencia evidenciaComVinculo(String tipoTarefa, StatusKanban status) {
        Tarefa t = new Tarefa();
        t.setId(5);
        t.setTitulo("Limpeza da sede");
        t.setPontuacao(15);
        t.setTipoTarefa(tipoTarefa);
        Unidade u = new Unidade();
        u.setId(3);
        u.setNome("Leões");
        TarefaUnidade tu = new TarefaUnidade();
        tu.setId(7);
        tu.setTarefa(t);
        tu.setUnidade(u);
        tu.setStatusKanban(status);
        Evidencia e = new Evidencia();
        e.setId(1);
        e.setUnidadeTarefa(tu);
        e.setUrlS3("https://s3/foto.png");
        return e;
    }

    private NotificacaoService notificacaoMock;

    private EvidenciaService evidenciaService(Evidencia e) {
        EvidenciaRepository repo = mock(EvidenciaRepository.class);
        when(repo.findById(1)).thenReturn(Optional.of(e));
        notificacaoMock = mock(NotificacaoService.class);
        return new EvidenciaService(repo, mock(TarefaUnidadeRepository.class), notificacaoMock);
    }

    @Test
    void aprovarEvidenciaConcluiVinculoEExpoeDadosDaTarefa() {
        Evidencia e = evidenciaComVinculo("CLUBE", StatusKanban.EM_REVISAO);
        EvidenciaAtualizacaoDto dto = new EvidenciaAtualizacaoDto();
        dto.setStatus("APROVADA");
        dto.setComentarioFeedback("Muito bem");
        dto.setPontuacao(12);

        EvidenciaResponseDto resp = evidenciaService(e).update(1, dto);

        assertEquals(StatusKanban.CONCLUIDA, e.getUnidadeTarefa().getStatusKanban());
        assertNotNull(e.getUnidadeTarefa().getDataConclusao());
        assertEquals("APROVADA", resp.getStatus());
        assertEquals("Limpeza da sede", resp.getTitulo());
        assertEquals("Leões", resp.getNomeUnidade());
        assertEquals(12, resp.getPontuacao());
        assertEquals(15, resp.getPontuacaoPrevista());
        assertEquals(12, e.getUnidadeTarefa().getPontuacaoConcedida());
        verify(notificacaoMock).notificarUnidade(eq(e.getUnidadeTarefa().getUnidade()), eq("Tarefa aprovada"),
                contains("+12 pontos"), eq(NotificacaoService.EVIDENCIA_APROVADA), eq(1));
        // atualização parcial não apaga a URL já enviada
        assertEquals("https://s3/foto.png", resp.getUrlS3());
    }

    @Test
    void solicitarCorrecaoDevolveTarefaParaEmAndamento() {
        Evidencia e = evidenciaComVinculo("CLUBE", StatusKanban.EM_REVISAO);
        EvidenciaAtualizacaoDto dto = new EvidenciaAtualizacaoDto();
        dto.setStatus("CORRECAO_SOLICITADA");
        dto.setComentarioFeedback("Falta foto do grupo");

        EvidenciaResponseDto resp = evidenciaService(e).update(1, dto);

        assertEquals(StatusKanban.EM_ANDAMENTO, e.getUnidadeTarefa().getStatusKanban());
        assertEquals("CORRECAO_SOLICITADA", resp.getStatus());
    }

    @Test
    void reconhecerCadernoRetornaStatusReconhecido() {
        Evidencia e = evidenciaComVinculo("CADERNO", StatusKanban.EM_REVISAO);
        EvidenciaAtualizacaoDto dto = new EvidenciaAtualizacaoDto();
        dto.setStatus("RECONHECIDO");

        assertEquals("RECONHECIDO", evidenciaService(e).update(1, dto).getStatus());
    }

    @Test
    void statusDeEvidenciaInvalidoEhRejeitado() {
        Evidencia e = evidenciaComVinculo("CLUBE", StatusKanban.EM_REVISAO);
        EvidenciaAtualizacaoDto dto = new EvidenciaAtualizacaoDto();
        dto.setStatus("QUALQUER");
        assertThrows(RequisicaoInvalidaException.class, () -> evidenciaService(e).update(1, dto));
    }

    // ---------- Cadastro via convite (/cadastro?token=...) ----------

    private Convite convite(String status, java.time.LocalDateTime expira) {
        Clube clube = new Clube(); clube.setId(1);
        Perfil perfil = new Perfil(); perfil.setNome("Conselheiro");
        Unidade unidade = new Unidade(); unidade.setId(3);
        Convite c = new Convite();
        c.setEmail("novo@clube.com");
        c.setToken("TOKEN123");
        c.setStatusConvite(status);
        c.setDataExpiracao(expira);
        c.setClube(clube);
        c.setPerfil(perfil);
        c.setUnidade(unidade);
        return c;
    }

    private UsuarioCriacaoDto cadastro(String email) {
        UsuarioCriacaoDto dto = new UsuarioCriacaoDto();
        dto.setNome("Novo");
        dto.setEmail(email);
        dto.setSenha("senha123");
        dto.setToken("TOKEN123");
        return dto;
    }

    private UsuarioService usuarioServiceCom(UsuarioRepository repo, ConviteRepository conviteRepo) {
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(encoder.encode(any())).thenReturn("HASH");
        return new UsuarioService(repo, mock(GerenciadorTokenJwt.class), mock(AuthenticationManager.class),
                encoder, mock(ClubeRepository.class), mock(PerfilRepository.class), conviteRepo);
    }

    @Test
    void cadastroComConviteValidoUsaDadosDoConviteEMarcaComoAceito() {
        UsuarioRepository repo = mock(UsuarioRepository.class);
        ConviteRepository conviteRepo = mock(ConviteRepository.class);
        Convite c = convite("PENDENTE", java.time.LocalDateTime.now().plusDays(3));
        when(conviteRepo.findByToken("TOKEN123")).thenReturn(Optional.of(c));
        when(repo.findByEmail("novo@clube.com")).thenReturn(Optional.empty());

        Usuario u = usuarioServiceCom(repo, conviteRepo).cadastrarUsuario(cadastro("NOVO@clube.com"));

        assertEquals("Conselheiro", u.getPerfil().getNome());
        assertEquals(1, u.getClube().getId());
        assertEquals(3, u.getUnidade().getId());
        assertEquals("HASH", u.getSenha());
        assertEquals("ACEITO", c.getStatusConvite());
    }

    @Test
    void cadastroComConviteExpiradoRetorna410() {
        ConviteRepository conviteRepo = mock(ConviteRepository.class);
        Convite c = convite("PENDENTE", java.time.LocalDateTime.now().minusDays(1));
        when(conviteRepo.findByToken("TOKEN123")).thenReturn(Optional.of(c));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> usuarioServiceCom(mock(UsuarioRepository.class), conviteRepo).cadastrarUsuario(cadastro("novo@clube.com")));
        assertEquals(410, ex.getStatusCode().value());
        assertEquals("EXPIRADO", c.getStatusConvite());
    }

    @Test
    void cadastroComEmailDiferenteDoConviteRetorna422() {
        ConviteRepository conviteRepo = mock(ConviteRepository.class);
        when(conviteRepo.findByToken("TOKEN123")).thenReturn(Optional.of(convite("PENDENTE", java.time.LocalDateTime.now().plusDays(1))));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> usuarioServiceCom(mock(UsuarioRepository.class), conviteRepo).cadastrarUsuario(cadastro("intruso@clube.com")));
        assertEquals(422, ex.getStatusCode().value());
    }

    @Test
    void cadastroSemTokenESemDadosRetorna400() {
        UsuarioCriacaoDto dto = cadastro("novo@clube.com");
        dto.setToken(null);
        assertThrows(RequisicaoInvalidaException.class,
                () -> usuarioServiceCom(mock(UsuarioRepository.class), mock(ConviteRepository.class)).cadastrarUsuario(dto));
    }

    // ---------- Perfil e senha ----------

    private Usuario usuario() {
        Usuario u = new Usuario();
        u.setId(1);
        u.setNome("Ana");
        u.setEmail("ana@clube.com");
        u.setSenha("HASH");
        return u;
    }

    @Test
    void atualizarPerfilComEmailDeOutroUsuarioRetornaConflito() {
        UsuarioRepository repo = mock(UsuarioRepository.class);
        when(repo.findByEmail("ana@clube.com")).thenReturn(Optional.of(usuario()));
        when(repo.findByEmail("outro@clube.com")).thenReturn(Optional.of(new Usuario()));
        UsuarioService service = new UsuarioService(repo, mock(GerenciadorTokenJwt.class), mock(AuthenticationManager.class),
                mock(PasswordEncoder.class), mock(ClubeRepository.class), mock(PerfilRepository.class), mock(ConviteRepository.class));

        UsuarioPerfilUpdateDto dto = new UsuarioPerfilUpdateDto();
        dto.setNome("Ana");
        dto.setEmail("outro@clube.com");

        assertThrows(EmailJaCadastradoException.class, () -> service.atualizarPerfil("ana@clube.com", dto));
    }

    @Test
    void atualizarPerfilPersisteTelefone() {
        UsuarioRepository repo = mock(UsuarioRepository.class);
        Usuario u = usuario();
        when(repo.findByEmail("ana@clube.com")).thenReturn(Optional.of(u));
        UsuarioService service = new UsuarioService(repo, mock(GerenciadorTokenJwt.class), mock(AuthenticationManager.class),
                mock(PasswordEncoder.class), mock(ClubeRepository.class), mock(PerfilRepository.class), mock(ConviteRepository.class));

        UsuarioPerfilUpdateDto dto = new UsuarioPerfilUpdateDto();
        dto.setNome("Ana Souza");
        dto.setEmail("ana@clube.com");
        dto.setTelefone("(11) 98888-7777");

        UsuarioResponseDto resp = service.atualizarPerfil("ana@clube.com", dto);
        assertEquals("Ana Souza", resp.getNome());
        assertEquals("(11) 98888-7777", resp.getTelefone());
    }

    @Test
    void alterarSenhaComSenhaAtualErradaRetorna401() {
        UsuarioRepository repo = mock(UsuarioRepository.class);
        when(repo.findByEmail("ana@clube.com")).thenReturn(Optional.of(usuario()));
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(encoder.matches("errada", "HASH")).thenReturn(false);
        UsuarioService service = new UsuarioService(repo, mock(GerenciadorTokenJwt.class), mock(AuthenticationManager.class),
                encoder, mock(ClubeRepository.class), mock(PerfilRepository.class), mock(ConviteRepository.class));

        UsuarioSenhaUpdateDto dto = new UsuarioSenhaUpdateDto();
        dto.setSenhaAtual("errada");
        dto.setNovaSenha("novaSenha123");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.alterarSenha("ana@clube.com", dto));
        assertEquals(401, ex.getStatusCode().value());
        verify(repo, never()).save(any());
    }

    @Test
    void alterarSenhaCorretaGravaHashNovo() {
        UsuarioRepository repo = mock(UsuarioRepository.class);
        Usuario u = usuario();
        when(repo.findByEmail("ana@clube.com")).thenReturn(Optional.of(u));
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(encoder.matches("atual", "HASH")).thenReturn(true);
        when(encoder.encode("novaSenha123")).thenReturn("HASH2");
        UsuarioService service = new UsuarioService(repo, mock(GerenciadorTokenJwt.class), mock(AuthenticationManager.class),
                encoder, mock(ClubeRepository.class), mock(PerfilRepository.class), mock(ConviteRepository.class));

        UsuarioSenhaUpdateDto dto = new UsuarioSenhaUpdateDto();
        dto.setSenhaAtual("atual");
        dto.setNovaSenha("novaSenha123");
        service.alterarSenha("ana@clube.com", dto);

        assertEquals("HASH2", u.getSenha());
    }
}
