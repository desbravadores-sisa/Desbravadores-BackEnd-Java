package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.Notificacao;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.domain.Usuario;
import school.sptech.APIDesbravadores.dto.NotificacaoResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.repository.NotificacaoRepository;
import school.sptech.APIDesbravadores.repository.UsuarioRepository;

import java.util.List;

@Service
public class NotificacaoService {

    public static final String EVIDENCIA_APROVADA = "EVIDENCIA_APROVADA";
    public static final String EVIDENCIA_CORRECAO = "EVIDENCIA_CORRECAO";

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository, UsuarioRepository usuarioRepository) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponseDto> listar(String emailUsuario) {
        return notificacaoRepository.findByUsuarioEmailOrderByIdDesc(emailUsuario).stream()
                .map(NotificacaoService::toResponse)
                .toList();
    }

    @Transactional
    public void marcarComoLida(Integer id, String emailUsuario) {
        Notificacao n = notificacaoRepository.findById(id)
                .filter(x -> x.getUsuario() != null && x.getUsuario().getEmail().equalsIgnoreCase(emailUsuario))
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Notificação não encontrada com ID: " + id));
        n.setLida(true);
        notificacaoRepository.save(n);
    }

    @Transactional
    public void marcarTodasComoLidas(String emailUsuario) {
        List<Notificacao> todas = notificacaoRepository.findByUsuarioEmailOrderByIdDesc(emailUsuario);
        todas.forEach(n -> n.setLida(true));
        notificacaoRepository.saveAll(todas);
    }

    /** Notifica todos os usuários ativos vinculados à unidade (ex.: o conselheiro). */
    @Transactional
    public void notificarUnidade(Unidade unidade, String titulo, String mensagem, String tipoReferencia, Integer idReferencia) {
        if (unidade == null || unidade.getId() == null) return;
        for (Usuario u : usuarioRepository.findByUnidadeId(unidade.getId())) {
            if (Boolean.FALSE.equals(u.getAtivo())) continue;
            Notificacao n = new Notificacao();
            n.setUsuario(u);
            n.setClube(u.getClube() != null ? u.getClube() : unidade.getClube());
            n.setTitulo(titulo);
            n.setMensagem(mensagem);
            n.setTipoReferencia(tipoReferencia);
            n.setIdReferencia(idReferencia);
            n.setLida(false);
            notificacaoRepository.save(n);
        }
    }

    static NotificacaoResponseDto toResponse(Notificacao n) {
        NotificacaoResponseDto dto = new NotificacaoResponseDto();
        dto.setId(n.getId());
        dto.setTitulo(n.getTitulo());
        dto.setMensagem(n.getMensagem());
        dto.setTipoReferencia(n.getTipoReferencia());
        dto.setIdReferencia(n.getIdReferencia());
        dto.setLida(Boolean.TRUE.equals(n.getLida()));
        dto.setDataCriacao(n.getDataCriacao());
        return dto;
    }
}
