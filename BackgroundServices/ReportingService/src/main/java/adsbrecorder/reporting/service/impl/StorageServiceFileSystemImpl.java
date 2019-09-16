package adsbrecorder.reporting.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import adsbrecorder.common.utils.NonSecureRandomUtils;
import adsbrecorder.reporting.service.StorageService;

@Service
public class StorageServiceFileSystemImpl implements StorageService, NonSecureRandomUtils {

    @Value("${adsbrecorder.report.data_root}")
    private String dataRootFolder;
    
    @Value("${adsbrecorder.report.output_root}")
    private String outputRootFolder;

    @Value("${adsbrecorder.report.tmpl_root}")
    private String templateRootFolder;

    @Value("${adsbrecorder.report.filename_collision_retry:100}")
    private int filenameCollisionMaxRetry;

    private File dataRoot;

    private File outputRoot;

    private File templateRoot;

    public StorageServiceFileSystemImpl() {
    }

    @PostConstruct
    public void checkFileSystem() {
        this.dataRoot = new File(dataRootFolder);
        if (!this.dataRoot.exists())
            this.dataRoot.mkdirs();
        this.templateRoot = new File(templateRootFolder);
        if (!this.templateRoot.exists())
            this.templateRoot.mkdirs();
        this.outputRoot = new File(outputRootFolder);
        if (!this.outputRoot.exists())
            this.outputRoot.mkdirs();
    }

    @Override
    public File createDataOutputFile(String extension) {
        return createOutputFile(this.dataRoot, extension, this.filenameCollisionMaxRetry);
    }

    @Override
    public File createReportOutputFile(String extension) {
        return createOutputFile(this.outputRoot, extension, this.filenameCollisionMaxRetry);
    }

    private File createOutputFile(File root, String extension, int maxRetry) {
        int retry = 0;
        while (retry < maxRetry) {
            String name = nextFilename(extension);
            File output = new File(root, name);
            if (!output.exists()) {
                try {
                    output.createNewFile();
                    return output;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            retry++;
        }
        throw new RuntimeException(String.format("Failed to create new output file after %d retries", this.filenameCollisionMaxRetry));
    }

    @Override
    public InputStream asInputStream(String filename) throws IOException {
        if (filename.toLowerCase().endsWith(".xsl")) {
            File xsl = new File(templateRoot, filename);
            if (xsl.exists())
                return new FileInputStream(xsl);
            return new ClassPathResource(filename).getInputStream();
        }
        if (filename.toLowerCase().endsWith(".xml")) {
            return new FileInputStream(new File(dataRoot, filename));
        }
        return new FileInputStream(new File(outputRootFolder, filename));
    }

    @Override
    public OutputStream asOutputStream(String filename) {
        return null;
    }
}
