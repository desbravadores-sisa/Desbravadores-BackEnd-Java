package school.sptech.APIDesbravadores.service;

import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.*;
import school.sptech.APIDesbravadores.dto.NotificacaoResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.repository.*;

@Service
@Transactional
public class NotificacaoService {
    private final NotificacaoRepository repository;
    private final UsuarioRepository usuarios;
    private final AcessoService acesso;

    public NotificacaoService(NotificacaoRepository repository, UsuarioRepository usuarios, AcessoService acesso) {
        this.repository = repository; this.usuarios = usuarios; this.acesso = acesso;
    }

    @Transactional(readOnly = true)
    public Page<NotificacaoResponseDto> listar(int page, int size) {
        return repository.findByUsuarioId(acesso.atual().getIdUsuario(), PageRequest.of(Math.max(0, page),
                Math.max(1, Math.min(size, 50)), Sort.by(Sort.Direction.DESC, "dataCriacao", "id")))
                .map(NotificacaoResponseDto::de);
    }

    @Transactional(readOnly = true)
    public long naoLidas() { return repository.countByUsuarioIdAndLidaFalse(acesso.atual().getIdUsuario()); }

    public void ler(Integer id) {
        var n = repository.findByIdAndUsuarioId(id, acesso.atual().getIdUsuario())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Notificação não encontrada."));
        n.setLida(true);
        repository.save(n);
    }

    public void lerTodas() { repository.marcarTodas(acesso.atual().getIdUsuario()); }

    public List<Usuario> conselheiros(Integer clubeId, Collection<Integer> unidadeIds) {
        return usuarios.findByClubeIdAndAtivo(clubeId, true).stream()
                .filter(u -> u.getPerfil() != null && "CONSELHEIRO".equalsIgnoreCase(u.getPerfil().getNome()))
                .filter(u -> u.getUnidade() != null && unidadeIds.contains(u.getUnidade().getId())).toList();
    }

    public void novasAtribuicoes(Tarefa tarefa, List<Unidade> unidades) {
        var nomes = unidades.stream().map(Unidade::getNome).toList();
        enviar(conselheiros(tarefa.getClube().getId(), unidades.stream().map(Unidade::getId).toList()),
                "NOVA_TAREFA", "Nova tarefa atribuída à sua unidade",
                "A tarefa \"" + tarefa.getNome() + "\" foi atribuída às unidades " + String.join(", ", nomes) + ".",
                tarefa.getId(), "TAREFA", "/minhas-tarefas?tarefa=" + tarefa.getId());
    }

    public void emRevisao(TarefaUnidade tu, String nome) {
        var destinatarios = usuarios.findByClubeIdAndAtivo(tu.getTarefa().getClube().getId(), true).stream()
                .filter(u -> u.getPerfil() != null && AcessoService.perfilDiretoria(u.getPerfil().getNome())).toList();
        enviar(destinatarios, "TAREFA_EM_REVISAO", "Tarefa aguardando revisão",
                nome + " enviou a tarefa \"" + tu.getTarefa().getNome() + "\" da unidade "
                        + tu.getUnidade().getNome() + " para revisão.",
                tu.getId(), "TAREFA_UNIDADE", "/evidencias?tarefaUnidade=" + tu.getId());
    }

    public List<Usuario> decisao(TarefaUnidade tu, boolean aprovada, String motivo) {
        var destinatarios = conselheiros(tu.getTarefa().getClube().getId(), List.of(tu.getUnidade().getId()));
        enviar(destinatarios, aprovada ? "EVIDENCIA_APROVADA" : "CORRECAO_SOLICITADA",
                aprovada ? "Tarefa aprovada" : "Correção solicitada",
                "A Diretoria " + (aprovada ? "aprovou a" : "solicitou uma correção na") + " tarefa \""
                        + tu.getTarefa().getNome() + "\"." + (motivo == null ? "" : " Motivo: " + motivo),
                tu.getId(), "TAREFA_UNIDADE", "/minhas-tarefas?tarefa=" + tu.getTarefa().getId());
        return destinatarios;
    }

    private void enviar(List<Usuario> destinatarios, String tipo, String titulo, String mensagem,
                        Integer referencia, String referenciaTipo, String url) {
        Set<Integer> enviados = new HashSet<>();
        for (var u : destinatarios) {
            if (!enviados.add(u.getId())) continue;
            var n = new Notificacao();
            n.setUsuario(u); n.setClube(u.getClube()); n.setTipo(tipo); n.setTitulo(titulo);
            n.setMensagem(mensagem); n.setIdReferencia(referencia); n.setTipoReferencia(referenciaTipo);
            n.setUrlDestino(url); repository.save(n);
        }
    }
}
