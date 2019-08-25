package adsbrecorder.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthorizationExpiredException extends RuntimeException {
    private static final long serialVersionUID = 5822835349212147800L;

    public AuthorizationExpiredException() {
        super("Re-authorization is required to access this resource.");
    }

    public AuthorizationExpiredException(String message) {
        super(message);
    }

    public AuthorizationExpiredException(Throwable cause) {
        super(cause);
    }

    public AuthorizationExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizationExpiredException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
