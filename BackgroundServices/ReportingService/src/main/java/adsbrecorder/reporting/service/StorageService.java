package adsbrecorder.reporting.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StorageService {

    default File createDataOutputFile() {
        return createDataOutputFile(null);
    }

    File createDataOutputFile(String extension);

    default File createReportOutputFile(String extension) {
        return createDataOutputFile(extension);
    }

    InputStream asInputStream(String filename) throws IOException;
    OutputStream asOutputStream(String filename) throws IOException;
}
