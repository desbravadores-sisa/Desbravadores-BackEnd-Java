package school.sptech.APIDesbravadores.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class ConviteDuplicadoException extends RuntimeException {
    public ConviteDuplicadoException(String message) {
        super(message);
    }
}
