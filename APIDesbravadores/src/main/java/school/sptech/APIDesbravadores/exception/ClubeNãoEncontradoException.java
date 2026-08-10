package school.sptech.APIDesbravadores.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ClubeNãoEncontradoException extends RuntimeException{

    public ClubeNãoEncontradoException() {
        super("Clube não encontrado");
    }
}
