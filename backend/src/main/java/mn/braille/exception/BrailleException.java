package mn.braille.exception;

import org.springframework.http.HttpStatus;

public class BrailleException extends RuntimeException {

    private final HttpStatus status;

    public BrailleException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
