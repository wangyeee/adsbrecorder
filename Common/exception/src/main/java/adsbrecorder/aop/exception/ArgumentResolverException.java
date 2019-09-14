package adsbrecorder.aop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ArgumentResolverException extends RuntimeException {
    private static final long serialVersionUID = 8209944983252555003L;

    public ArgumentResolverException() {
        this("Failed to resolve argument");
    }

    public ArgumentResolverException(String message) {
        super(message);
    }

    public ArgumentResolverException(Throwable cause) {
        super(cause);
    }

    public ArgumentResolverException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArgumentResolverException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
