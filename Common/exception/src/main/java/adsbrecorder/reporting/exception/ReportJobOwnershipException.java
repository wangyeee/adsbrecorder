package adsbrecorder.reporting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import adsbrecorder.security.exception.ObjectOwnershipException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ReportJobOwnershipException extends ObjectOwnershipException {
    private static final long serialVersionUID = -3252711476751580600L;

    public ReportJobOwnershipException() {
        super("A user can only view his own reports.");
    }

    public ReportJobOwnershipException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ReportJobOwnershipException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportJobOwnershipException(String message) {
        super(message);
    }

    public ReportJobOwnershipException(Throwable cause) {
        super(cause);
    }
}
