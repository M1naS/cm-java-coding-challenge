package exception;

import org.springframework.http.HttpStatus;

public class SerializationException extends AppException {
    public SerializationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
