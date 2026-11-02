package school.sptech.APIDesbravadores.exception;

public class SenhaAtualIncorretaException extends RuntimeException {
    public SenhaAtualIncorretaException() {
        super("Senha atual incorreta.");
    }
}