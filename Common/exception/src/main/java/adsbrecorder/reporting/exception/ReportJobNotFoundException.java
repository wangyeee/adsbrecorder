package adsbrecorder.reporting.exception;

import java.math.BigInteger;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReportJobNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -7462877168467175195L;

    public ReportJobNotFoundException() {
        super("Report job not found");
    }

    public ReportJobNotFoundException(BigInteger id) {
        super(String.format("Report job %s not found", id.toString()));
    }

    public ReportJobNotFoundException(String message) {
        super(message);
    }

    public ReportJobNotFoundException(Throwable cause) {
        super(cause);
    }

    public ReportJobNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportJobNotFoundException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
