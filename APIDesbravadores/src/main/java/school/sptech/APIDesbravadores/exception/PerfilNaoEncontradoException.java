package school.sptech.APIDesbravadores.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND,reason = "O perfil do usuário não foi encontrado")
public class PerfilNaoEncontradoException extends RuntimeException{
}
