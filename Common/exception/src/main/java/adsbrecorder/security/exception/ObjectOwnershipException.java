package adsbrecorder.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ObjectOwnershipException extends RuntimeException {
    private static final long serialVersionUID = -8901793945284824274L;

    public ObjectOwnershipException() {
        super("Ownership validation failure");
    }

    public ObjectOwnershipException(String message) {
        super(message);
    }

    public ObjectOwnershipException(Throwable cause) {
        super(cause);
    }

    public ObjectOwnershipException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectOwnershipException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
