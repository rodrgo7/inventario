package oliveiradev.inventario.interfaces.exceptionhandler;

import oliveiradev.inventario.application.exception.EmailJaCadastradoException;
import oliveiradev.inventario.application.exception.RecursoNaoEncontradoException;
import oliveiradev.inventario.application.exception.RegraDeNegocioException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice // Torna esta classe um handler global de exceções para os controllers
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    public record ApiErrorResponse(
            Integer status,
            OffsetDateTime timestamp,
            String type,
            String title,
            String detail,
            List<ApiErrorField> fields
    ) {}

    public record ApiErrorField(
            String name,
            String message) {}

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Object> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                OffsetDateTime.now(),
                "urn:oliveiradev:erro:recurso-nao-encontrado",
                "Recurso não encontrado",
                ex.getMessage(),
                null);
        logger.warn("Recurso não encontrado: {}", ex.getMessage());
        return handleExceptionInternal(ex, body, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<Object> handleEmailJaCadastrado(EmailJaCadastradoException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                OffsetDateTime.now(),
                "urn:oliveiradev:erro:email-ja-cadastrado",
                "Email já cadastrado",
                ex.getMessage(),
                null);
        logger.warn("Email já cadastrado: {}", ex.getMessage());
        return handleExceptionInternal(ex, body, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<Object> handleRegraDeNegocio(RegraDeNegocioException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                OffsetDateTime.now(),
                "urn:oliveiradev:erro:regra-de-negocio",
                "Violação de regra de negócio",
                ex.getMessage(),
                null);
        logger.warn("Regra de negócio violada: {}", ex.getMessage());
        return handleExceptionInternal(ex, body, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                OffsetDateTime.now(),
                "urn:oliveiradev:erro:argumento-invalido",
                "Argumento inválido",
                ex.getMessage(),
                null);
        logger.warn("Argumento ilegal: {}", ex.getMessage());
        return handleExceptionInternal(ex, body, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<ApiErrorField> problemFields = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ApiErrorField(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());

        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                OffsetDateTime.now(),
                "urn:oliveiradev:erro:validacao",
                "Um ou mais campos são inválidos.",
                "Verifique os campos da requisição.",
                problemFields);
        logger.warn("Erro de validação de DTO: {}", problemFields);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                OffsetDateTime.now(),
                "urn:oliveiradev:erro:erro-interno",
                "Erro interno do servidor",
                "Ocorreu um erro inesperado no processamento da sua requisição.",
                null);
        logger.error("Erro interno não tratado: ", ex); // Logar o stack trace completo no servidor
        return handleExceptionInternal(ex, body, new HttpHeaders(), status, request);
    }
}