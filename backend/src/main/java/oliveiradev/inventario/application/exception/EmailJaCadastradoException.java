package oliveiradev.inventario.application.exception;

public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException(String email) {
        super("O email '" + email + "' já está cadastrado no sistema.");
    }

    public EmailJaCadastradoException(String email, Throwable causa) {
        super("O email '" + email + "' já está cadastrado no sistema.", causa);
    }
}