package AfetYonetimSistemi.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 404 Not Found ──────────────────────────────────────────────────────────

    @ExceptionHandler({
            NotFoundUserException.class,
            NotFoundUserTalepException.class,
            NotFoundTalep.class,
            NotFoundDepo.class,
            NotFoundDepoMalzemeException.class,
            NotFoundMalzemeException.class,
            NotFoundAfetException.class,
            NotFoundGorevliException.class,
            NotFoundSanalDepoException.class,
            NotFoundILException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ── 400 Bad Request ────────────────────────────────────────────────────────

    @ExceptionHandler({
            InvalidPasswordException.class,
            InvalidTcException.class,
            InvalidTelNoException.class,
            InvalidTelNoTcException.class,
            InvalidKoordinatException.class,
            InvalidDepoMalzemeException.class,
            InvalidKaynakTalepException.class,
            InvalidKodException.class,
            InvalidTimeLimitedException.class,
            NotLimitPinsException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ── Validation (@Valid) ────────────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validasyon hatası")
                .timestamp(LocalDateTime.now())
                .errors(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    // ── Fallback ───────────────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Beklenmeyen bir hata oluştu");
    }

    // ── Yardımcı ───────────────────────────────────────────────────────────────

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
                ErrorResponse.builder()
                        .status(status.value())
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}