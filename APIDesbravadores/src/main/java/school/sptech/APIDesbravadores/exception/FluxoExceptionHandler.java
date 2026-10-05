package school.sptech.APIDesbravadores.exception;

import java.util.Map;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class FluxoExceptionHandler {
    @ExceptionHandler({RequisicaoInvalidaException.class, AcessoNegadoException.class,
            EntidadeNaoEncontradaException.class, RegraNegocioException.class,
            ConviteDuplicadoException.class, ConviteExpiradoException.class,
            ConviteConflitoEstadoException.class})
    public ResponseEntity<Map<String, String>> tratar(RuntimeException exception) {
        var annotation = AnnotatedElementUtils.findMergedAnnotation(exception.getClass(), ResponseStatus.class);
        var status = exception instanceof ResponseStatusException response ? response.getStatusCode()
                : annotation == null ? HttpStatus.BAD_REQUEST : annotation.code();
        var mensagem = exception instanceof ResponseStatusException response ? response.getReason() : exception.getMessage();
        return ResponseEntity.status(status).body(Map.of("message", mensagem == null ? "Operação inválida." : mensagem));
    }
}
