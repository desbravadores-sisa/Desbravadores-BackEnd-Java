package school.sptech.APIDesbravadores.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ConviteNãoEncontradoException extends RuntimeException{
    public ConviteNãoEncontradoException() {
        super("O convite solicitado não foi encontrado ou o token é inválido.");
    }

    public ConviteNãoEncontradoException(String message) {
        super(message);
    }
}
