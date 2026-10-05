package school.sptech.APIDesbravadores.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.*;
import school.sptech.APIDesbravadores.dto.*;
import school.sptech.APIDesbravadores.exception.*;
import school.sptech.APIDesbravadores.mapper.EvidenciaMapper;
import school.sptech.APIDesbravadores.repository.*;

@Service
@Transactional
public class EvidenciaService {
    private final EvidenciaRepository evidencias;
    private final TarefaUnidadeRepository vinculos;
    private final UsuarioRepository usuarios;
    private final TarefaService tarefas;
    private final AcessoService acesso;
    private final NotificacaoService notificacoes;
    private final EmailService emails;

    public EvidenciaService(EvidenciaRepository evidencias, TarefaUnidadeRepository vinculos,
            UsuarioRepository usuarios, TarefaService tarefas, AcessoService acesso,
            NotificacaoService notificacoes, EmailService emails) {
        this.evidencias = evidencias; this.vinculos = vinculos; this.usuarios = usuarios;
        this.tarefas = tarefas; this.acesso = acesso; this.notificacoes = notificacoes; this.emails = emails;
    }

    private void validarUnidade(Integer id) {
        acesso.exigirConselheiro();
        if (!Objects.equals(id, acesso.atual().getIdUnidade())) throw new AcessoNegadoException("Unidade não autorizada.");
    }

    public EvidenciaResponseDto create(EvidenciaCreateDto dto, Integer unidadeId) {
        validarUnidade(unidadeId);
        var tu = tarefas.buscarDaUnidade(dto.getIdTarefa());
        editavel(tu);
        var evidencia = EvidenciaMapper.toEntity(dto, tu);
        return EvidenciaMapper.toResponseDto(evidencias.save(evidencia));
    }

    private void editavel(TarefaUnidade tu) {
        if (tu.getStatusKanban() == StatusKanban.CONCLUIDO || tu.getStatusKanban() == StatusKanban.EM_REVISAO)
            throw new RequisicaoInvalidaException("A evidência não pode ser alterada durante a revisão ou após a conclusão.");
    }

    @Transactional(readOnly = true)
    public List<EvidenciaResponseDto> findAllByClube(Integer clubeId) {
        acesso.exigirDiretoria(); acesso.validarClube(clubeId);
        return evidencias.findAllByTarefaUnidadeTarefaClubeId(clubeId).stream()
                .filter(e -> e.getTarefaUnidade().getStatusKanban() == StatusKanban.EM_REVISAO)
                .map(EvidenciaMapper::toResponseDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EvidenciaResponseDto> findAllByUnidade(Integer unidadeId) {
        validarUnidade(unidadeId);
        return evidencias.findAllByTarefaUnidadeUnidadeId(unidadeId).stream()
                .filter(e -> e.getTarefaUnidade().getTarefa().getClube().getId().equals(acesso.atual().getIdClube()))
                .map(EvidenciaMapper::toResponseDto).toList();
    }

    private Evidencia buscar(Integer id) {
        var e = evidencias.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Evidência não encontrada."));
        // Toda mutação usa o mesmo bloqueio, inclusive revisão de duas evidências do mesmo vínculo.
        var tu = vinculos.buscarBloqueado(e.getTarefaUnidade().getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada."));
        acesso.validarVinculo(tu); e.setTarefaUnidade(tu); return e;
    }

    public EvidenciaResponseDto update(Integer id, EvidenciaUpdateDto dto, Integer unidadeId) {
        validarUnidade(unidadeId); var e = buscar(id); editavel(e.getTarefaUnidade());
        EvidenciaMapper.updateEntity(dto, e);
        return EvidenciaMapper.toResponseDto(evidencias.save(e));
    }

    public void delete(Integer id, Integer unidadeId) {
        validarUnidade(unidadeId); var e = buscar(id); editavel(e.getTarefaUnidade());
        evidencias.delete(e);
    }

    public EvidenciaResponseDto aprovar(Integer id, AprovacaoRequestDto dto) {
        acesso.exigirDiretoria(); var e = buscar(id); var tu = e.getTarefaUnidade();
        if (tu.getStatusKanban() == StatusKanban.CONCLUIDO) return EvidenciaMapper.toResponseDto(e);
        if (tu.getStatusKanban() != StatusKanban.EM_REVISAO) throw new RequisicaoInvalidaException("A tarefa deve estar em revisão.");
        var pontos = dto.pontuacao() == null ? tu.getTarefa().getPontuacao() : dto.pontuacao();
        if (pontos == null || pontos < 0) throw new RequisicaoInvalidaException("Pontuação inválida.");
        var revisor = usuarios.getReferenceById(acesso.atual().getIdUsuario());
        var agora = LocalDateTime.now();
        tu.setStatusKanban(StatusKanban.CONCLUIDO); tu.setPontuacaoConcedida(pontos);
        tu.setRevisor(revisor); tu.setDataConclusao(agora);
        e.setRevisor(revisor); e.setDataAnalise(agora); e.setComentarioFeedback(null);
        vinculos.save(tu); evidencias.save(e);
        for (var usuario : notificacoes.decisao(tu, true, null)) emails.sendTaskApprovedEmail(usuario, tu);
        return EvidenciaMapper.toResponseDto(e);
    }

    public EvidenciaResponseDto solicitarCorrecao(Integer id, CorrecaoRequestDto dto) {
        acesso.exigirDiretoria(); var e = buscar(id); var tu = e.getTarefaUnidade();
        if (tu.getStatusKanban() == StatusKanban.EM_ANDAMENTO && Objects.equals(e.getComentarioFeedback(), dto.justificativa().trim()))
            return EvidenciaMapper.toResponseDto(e);
        if (tu.getStatusKanban() != StatusKanban.EM_REVISAO) throw new RequisicaoInvalidaException("A tarefa deve estar em revisão.");
        e.setComentarioFeedback(dto.justificativa().trim());
        e.setRevisor(usuarios.getReferenceById(acesso.atual().getIdUsuario())); e.setDataAnalise(LocalDateTime.now());
        tu.setStatusKanban(StatusKanban.EM_ANDAMENTO); vinculos.save(tu); evidencias.save(e);
        notificacoes.decisao(tu, false, e.getComentarioFeedback());
        return EvidenciaMapper.toResponseDto(e);
    }
}
