package school.sptech.APIDesbravadores.dto;

import java.time.LocalDateTime;
import school.sptech.APIDesbravadores.domain.Notificacao;

public record NotificacaoResponseDto(Integer id, Integer usuarioId, String tipo, String titulo,
        String mensagem, Boolean lida, LocalDateTime dataCriacao, Integer referenciaId,
        String referenciaTipo, String urlDestino) {
    public static NotificacaoResponseDto de(Notificacao n) {
        return new NotificacaoResponseDto(n.getId(), n.getUsuario().getId(), n.getTipo(), n.getTitulo(),
                n.getMensagem(), n.getLida(), n.getDataCriacao(), n.getIdReferencia(),
                n.getTipoReferencia(), n.getUrlDestino());
    }
}
