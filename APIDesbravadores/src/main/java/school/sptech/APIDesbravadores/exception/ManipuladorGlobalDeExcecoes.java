package school.sptech.APIDesbravadores.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.sptech.APIDesbravadores.dto.ErroRespostaDto;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Garante que todo erro previsto saia com um corpo explicando o motivo, em vez do
 * 500 genérico que o Spring devolve para exceções sem tratamento.
 *
 * Não existe handler para Exception aqui de propósito: um catch-all transformaria
 * os 401/403 do Spring Security em 500.
 */
@RestControllerAdvice
public class ManipuladorGlobalDeExcecoes {

    @ExceptionHandler({
            EntidadeNaoEncontradaException.class,
            ClubeNãoEncontradoException.class,
            UnidadeNãoEncontradaException.class
    })
    public ResponseEntity<ErroRespostaDto> tratarNaoEncontrado(RuntimeException excecao, HttpServletRequest requisicao) {
        return montar(HttpStatus.NOT_FOUND, excecao.getMessage(), requisicao, null);
    }

    @ExceptionHandler({
            EmailJaCadastradoException.class,
            UnidadeJácadastradaException.class
    })
    public ResponseEntity<ErroRespostaDto> tratarConflito(RuntimeException excecao, HttpServletRequest requisicao) {
        return montar(HttpStatus.CONFLICT, excecao.getMessage(), requisicao, null);
    }

    @ExceptionHandler({
            RequisicaoInvalidaException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErroRespostaDto> tratarRequisicaoInvalida(RuntimeException excecao, HttpServletRequest requisicao) {
        return montar(HttpStatus.BAD_REQUEST, excecao.getMessage(), requisicao, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDto> tratarValidacao(MethodArgumentNotValidException excecao, HttpServletRequest requisicao) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError erro : excecao.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(erro.getField(), erro.getDefaultMessage());
        }
        return montar(HttpStatus.BAD_REQUEST, "Dados de entrada inválidos", requisicao, campos);
    }

    private ResponseEntity<ErroRespostaDto> montar(HttpStatus status, String mensagem,
                                                   HttpServletRequest requisicao, Map<String, String> campos) {
        ErroRespostaDto corpo = new ErroRespostaDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                requisicao.getRequestURI(),
                campos
        );
        return ResponseEntity.status(status).body(corpo);
    }
}
