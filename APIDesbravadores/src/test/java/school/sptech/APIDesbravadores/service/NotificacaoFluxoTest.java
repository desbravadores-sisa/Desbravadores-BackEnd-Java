package school.sptech.APIDesbravadores.service;

import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.*;
import school.sptech.APIDesbravadores.dto.*;
import school.sptech.APIDesbravadores.exception.*;
import school.sptech.APIDesbravadores.repository.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class NotificacaoFluxoTest {
    @Autowired TarefaService tarefas;
    @Autowired UnidadeService unidadeService;
    @Autowired EvidenciaService evidencias;
    @Autowired ConviteService convites;
    @Autowired NotificacaoService notificacoes;
    @Autowired NotificacaoRepository notificacaoRepository;
    @Autowired EmailPendenteRepository emailRepository;
    @Autowired ClubeRepository clubes;
    @Autowired UnidadeRepository unidades;
    @Autowired UsuarioRepository usuarios;
    @Autowired PerfilRepository perfis;
    @Autowired CicloRepository ciclos;
    @Autowired TarefaUnidadeRepository vinculos;
    @Autowired EvidenciaRepository evidenciaRepository;
    @Autowired TarefaRepository tarefaRepository;
    @Autowired MockMvc mvc;
    @Autowired school.sptech.APIDesbravadores.config.GerenciadorTokenJwt jwt;
    @Autowired org.springframework.transaction.PlatformTransactionManager transactionManager;
    private Clube clube;
    private Unidade tigresas, leoes;
    private Usuario diretor, vice, conselheiro, outro;
    private Perfil perfilConselheiro;

    @BeforeEach void preparar() {
        clube = new Clube(); clube.setNome("Tigre da Montanha"); clubes.save(clube);
        var ciclo = new Ciclo(); ciclo.setNome("2026"); ciclo.setClube(clube); ciclos.save(ciclo);
        tigresas = unidade("Tigresas"); leoes = unidade("Leões");
        var perfilDiretoria = perfil("DIRETORIA");
        var perfilVice = perfil("VICE-DIRETOR");
        perfilConselheiro = perfil("CONSELHEIRO");
        diretor = usuario("Ana", perfilDiretoria, null);
        vice = usuario("Vice", perfilVice, null);
        conselheiro = usuario("Carlos", perfilConselheiro, tigresas);
        outro = usuario("Pedro", perfilConselheiro, leoes);
        autenticar(diretor);
    }
    @AfterEach void limparSessao() { SecurityContextHolder.clearContext(); }
    private Perfil perfil(String nome) {
        return perfis.findAll().stream().filter(p -> p.getNome().equals(nome)).findFirst().orElseGet(() -> {
            var p = new Perfil(); p.setNome(nome); return perfis.save(p);
        });
    }
    private Unidade unidade(String nome) { var u = new Unidade(); u.setNome(nome); u.setClube(clube); return unidades.save(u); }
    private Usuario usuario(String nome, Perfil perfil, Unidade unidade) {
        var u = new Usuario(); u.setNome(nome); u.setEmail(nome.toLowerCase() + UUID.randomUUID() + "@tigre.com");
        u.setSenha("senha"); u.setAtivo(true); u.setClube(clube); u.setPerfil(perfil); u.setUnidade(unidade); return usuarios.save(u);
    }
    private UsuarioDetalhesDto principal(Usuario u) { return new UsuarioDetalhesDto(u); }
    private void autenticar(Usuario u) {
        var p = principal(u);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(p, null, p.getAuthorities()));
    }
    private TarefaCreateDto request(boolean todas) {
        var dto = new TarefaCreateDto(); dto.setNome("Campori Regional"); dto.setPontuacao(300);
        dto.setTodasUnidades(todas); dto.setUnidadeIds(List.of(tigresas.getId())); dto.setRequestId(UUID.randomUUID().toString());
        return dto;
    }
    private TarefaUnidade tarefaEmAndamento() {
        var response = tarefas.create(request(false));
        var tu = vinculos.findAllByTarefaId(response.getId()).getFirst();
        autenticar(conselheiro);
        tarefas.updateStatus(response.getId(), "EM_ANDAMENTO");
        var dto = new EvidenciaCreateDto(); dto.setIdTarefa(response.getId()); dto.setNome("Autorizações");
        dto.setUrlAnexo("https://exemplo.com/autorizacoes.pdf");
        evidencias.create(dto, tigresas.getId()); return tu;
    }
    private Evidencia enviarParaRevisao() {
        var tu = tarefaEmAndamento(); tarefas.updateStatus(tu.getTarefa().getId(), "EM_REVISAO");
        autenticar(diretor); return evidenciaRepository.findFirstByTarefaUnidadeIdOrderByIdDesc(tu.getId()).orElseThrow();
    }
    private long quantidade(Usuario u) { return notificacaoRepository.countByUsuarioIdAndLidaFalse(u.getId()); }

    @Test void tarefaParaUmaUnidadeNotificaSomenteConselheiroCorreto() {
        tarefas.create(request(false));
        assertEquals(1, quantidade(conselheiro)); assertEquals(0, quantidade(outro)); assertEquals(0, quantidade(diretor));
    }
    @Test void tarefaGeralNotificaTodasUnidadesSemDuplicarUsuario() {
        tarefas.create(request(true));
        assertEquals(1, quantidade(conselheiro)); assertEquals(1, quantidade(outro));
    }
    @Test void conselheiroInativoNaoRecebe() {
        outro.setAtivo(false); usuarios.save(outro); tarefas.create(request(true)); assertEquals(0, quantidade(outro));
    }
    @Test void retryDaCriacaoNaoDuplicaTarefaOuNotificacao() {
        var dto = request(false);
        var a = tarefas.create(dto); var b = tarefas.create(dto);
        assertEquals(a.getId(), b.getId()); assertEquals(1, quantidade(conselheiro));
    }
    @Test void editarSemNovaUnidadeNaoNotificaENovasUnidadesRecebemUmaVez() {
        var response = tarefas.create(request(false));
        var edit = new TarefaUpdateDto(); edit.setNome("Campori editado"); edit.setPontuacao(300);
        tarefas.update(response.getId(), edit); assertEquals(1, quantidade(conselheiro));
        edit.setUnidadeIds(List.of(tigresas.getId(), leoes.getId(), leoes.getId()));
        tarefas.update(response.getId(), edit); tarefas.update(response.getId(), edit);
        assertEquals(1, quantidade(conselheiro)); assertEquals(1, quantidade(outro));
    }
    @Test void revisaoNotificaDiretoriaERepeticaoNaoDuplica() {
        var tu = tarefaEmAndamento();
        tarefas.updateStatus(tu.getTarefa().getId(), "EM_REVISAO");
        tarefas.updateStatus(tu.getTarefa().getId(), "Em Revisão");
        assertEquals(1, quantidade(diretor)); assertEquals(1, quantidade(vice));
        assertEquals(1, quantidade(conselheiro)); assertEquals(0, quantidade(outro));
    }
    @Test void revisaoExigeEvidenciaEConselheiroNaoConcluiDiretamente() {
        var response = tarefas.create(request(false)); autenticar(conselheiro);
        assertThrows(RequisicaoInvalidaException.class, () -> tarefas.updateStatus(response.getId(), "EM_REVISAO"));
        assertThrows(RequisicaoInvalidaException.class, () -> tarefas.updateStatus(response.getId(), "CONCLUIDO"));
    }
    @Test void aprovarNotificaEnfileiraEmailRegistraRevisorEPontosSemDuplicar() {
        var e = enviarParaRevisao();
        evidencias.aprovar(e.getId(), new AprovacaoRequestDto(280));
        evidencias.aprovar(e.getId(), new AprovacaoRequestDto(280));
        assertEquals(StatusKanban.CONCLUIDO, e.getTarefaUnidade().getStatusKanban());
        assertEquals(280, vinculos.pontuacaoUnidade(tigresas.getId()));
        var unidade = unidadeService.buscarUnidadePorId(tigresas.getId());
        assertEquals(280, unidade.getPontuacao()); assertEquals(1, unidade.getTarefasConcluidas());
        assertEquals(diretor.getId(), e.getRevisor().getId()); assertNotNull(e.getDataAnalise());
        assertEquals(2, quantidade(conselheiro)); assertEquals(0, quantidade(outro));
        assertEquals(1, emailRepository.count());
        var mail = emailRepository.findAll().getFirst();
        assertEquals(conselheiro.getEmail(), mail.getDestinatario());
        assertTrue(mail.getConteudo().contains("280")); assertTrue(mail.getConteudo().contains("Tigresas"));
    }
    @Test void correcaoSalvaMotivoENotificaSemRepetirEPodeReenviar() {
        var e = enviarParaRevisao();
        evidencias.solicitarCorrecao(e.getId(), new CorrecaoRequestDto("Documento ausente"));
        evidencias.solicitarCorrecao(e.getId(), new CorrecaoRequestDto("Documento ausente"));
        assertEquals("Documento ausente", e.getComentarioFeedback());
        assertEquals(StatusKanban.EM_ANDAMENTO, e.getTarefaUnidade().getStatusKanban());
        assertEquals(2, quantidade(conselheiro)); assertEquals(0, emailRepository.count());
        autenticar(conselheiro);
        tarefas.updateStatus(e.getTarefaUnidade().getTarefa().getId(), "EM_REVISAO");
        assertEquals(2, quantidade(diretor));
    }
    @Test void notificacoesSomenteDoUsuarioContadorLeituraIndividualETodas() {
        tarefas.create(request(true)); autenticar(conselheiro);
        assertEquals(1, notificacoes.naoLidas());
        var pagina = notificacoes.listar(0, 100); assertEquals(1, pagina.getTotalElements());
        var id = pagina.getContent().getFirst().id(); notificacoes.ler(id); notificacoes.ler(id);
        assertEquals(0, notificacoes.naoLidas());
        autenticar(outro); assertThrows(EntidadeNaoEncontradaException.class, () -> notificacoes.ler(id));
        assertEquals(1, notificacoes.naoLidas()); notificacoes.lerTodas(); assertEquals(0, notificacoes.naoLidas());
    }
    @Test void conselheiroNaoAcessaTarefaDaOutraUnidadeOuCriaConvite() {
        var response = tarefas.create(request(false)); autenticar(outro);
        assertThrows(EntidadeNaoEncontradaException.class, () -> tarefas.updateStatus(response.getId(), "EM_ANDAMENTO"));
        assertThrows(AcessoNegadoException.class, () -> tarefas.create(request(true)));
        var dto = new ConviteCriacaoRequestDto(); dto.setEmail("novo@tigre.com"); dto.setIdPerfil(perfilConselheiro.getId());
        assertThrows(AcessoNegadoException.class, () -> convites.criarConvite(dto, clube.getId()));
    }
    @Test void conviteEnfileiraEmailComFuncaoUnidadeLinkEValidade() {
        var dto = new ConviteCriacaoRequestDto(); dto.setEmail("novo@tigre.com"); dto.setIdPerfil(perfilConselheiro.getId());
        dto.setIdUnidade(tigresas.getId()); var c = convites.criarConvite(dto, clube.getId());
        var email = emailRepository.findAll().getFirst();
        assertEquals("PENDENTE", c.getStatusConvite()); assertTrue(email.getConteudo().contains(c.getToken()));
        assertTrue(email.getConteudo().contains("CONSELHEIRO")); assertTrue(email.getConteudo().contains("Tigresas"));
        assertTrue(email.getConteudo().contains("Válido até"));
        assertThrows(ConviteDuplicadoException.class, () -> convites.criarConvite(dto, clube.getId()));
    }
    @Test void naoPermiteAtribuicaoParaOutroClube() {
        var outroClube = new Clube(); outroClube.setNome("Outro"); clubes.save(outroClube);
        var unidade = new Unidade(); unidade.setNome("Outra"); unidade.setClube(outroClube); unidades.save(unidade);
        var dto = request(false); dto.setUnidadeIds(List.of(unidade.getId()));
        assertThrows(AcessoNegadoException.class, () -> tarefas.create(dto));
        assertEquals(0, tarefaRepository.count());
    }
    @Test void endpointsExigemAutenticacaoEPermissaoReal() throws Exception {
        SecurityContextHolder.clearContext();
        mvc.perform(get("/api/notifications")).andExpect(status().isUnauthorized());
        mvc.perform(get("/tarefas/kanban")).andExpect(status().isUnauthorized());
        mvc.perform(post("/convites").with(user(principal(conselheiro))).contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"novo@tigre.com\",\"idPerfil\":" + perfilConselheiro.getId() + "}"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/notifications/unread-count").with(user(principal(conselheiro))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.count").value(0));
    }
    @Test void endpointNaoPermiteLerNotificacaoDeOutroUsuario() throws Exception {
        tarefas.create(request(true)); autenticar(conselheiro);
        var id = notificacoes.listar(0, 20).getContent().getFirst().id();
        mvc.perform(patch("/api/notifications/" + id + "/read").with(user(principal(outro)))).andExpect(status().isNotFound());
        mvc.perform(patch("/api/notifications/" + id + "/read").with(user(principal(conselheiro)))).andExpect(status().isNoContent());
    }

    @Test void cookieJwtFuncionaEUsuarioInativadoPerdeAcesso() throws Exception {
        tarefas.create(request(false));
        var principal = principal(conselheiro);
        var token = jwt.generateToken(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        SecurityContextHolder.clearContext();
        mvc.perform(get("/api/notifications/unread-count").cookie(new jakarta.servlet.http.Cookie("authToken", token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.count").value(1));
        conselheiro.setAtivo(false); usuarios.saveAndFlush(conselheiro);
        SecurityContextHolder.clearContext();
        mvc.perform(get("/api/notifications").cookie(new jakarta.servlet.http.Cookie("authToken", token)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
    void chamadasSimultaneasDeRevisaoEAprovacaoNaoDuplicam() throws Exception {
        var tu = tarefaEmAndamento();
        var taskId = tu.getTarefa().getId();
        var evidenciaId = evidenciaRepository.findFirstByTarefaUnidadeIdOrderByIdDesc(tu.getId()).orElseThrow().getId();
        try {
            executarSimultaneamente(conselheiro, () -> tarefas.updateStatus(taskId, "EM_REVISAO"));
            assertEquals(1, quantidade(diretor)); assertEquals(1, quantidade(vice));
            executarSimultaneamente(diretor, () -> evidencias.aprovar(evidenciaId, new AprovacaoRequestDto(280)));
            assertEquals(2, quantidade(conselheiro)); assertEquals(1, emailRepository.count());
            assertEquals(280, vinculos.pontuacaoUnidade(tigresas.getId()));
        } finally {
            new org.springframework.transaction.support.TransactionTemplate(transactionManager).execute(status -> {
                notificacaoRepository.deleteAll(notificacaoRepository.findAll().stream()
                        .filter(n -> n.getClube().getId().equals(clube.getId())).toList());
                emailRepository.deleteAll(); evidenciaRepository.deleteById(evidenciaId);
                vinculos.deleteAll(vinculos.findAllByTarefaId(taskId)); tarefaRepository.deleteById(taskId);
                usuarios.deleteAll(List.of(diretor, vice, conselheiro, outro)); unidades.deleteAll(List.of(tigresas, leoes));
                ciclos.deleteAll(ciclos.findByClubeIdAndAtivoTrue(clube.getId())); clubes.delete(clube);
                return null;
            });
        }
    }

    private void executarSimultaneamente(Usuario usuario, Runnable acao) throws Exception {
        var liberar = new java.util.concurrent.CountDownLatch(1);
        try (var executor = java.util.concurrent.Executors.newFixedThreadPool(2)) {
            var futures = new java.util.ArrayList<java.util.concurrent.Future<?>>();
            for (int i = 0; i < 2; i++) futures.add(executor.submit(() -> {
                autenticar(usuario);
                try { liberar.await(); acao.run(); }
                catch (InterruptedException ex) { Thread.currentThread().interrupt(); throw new RuntimeException(ex); }
                finally { SecurityContextHolder.clearContext(); }
            }));
            liberar.countDown();
            for (var future : futures) future.get(15, java.util.concurrent.TimeUnit.SECONDS);
        }
    }
}
