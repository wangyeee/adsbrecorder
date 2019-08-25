package adsbrecorder.reporting.service.impl;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.PostConstruct;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import adsbrecorder.reporting.entity.ReportJob;
import adsbrecorder.reporting.repo.ReportJobRepository;
import adsbrecorder.reporting.service.RenderService;
import adsbrecorder.reporting.service.StorageService;

@Service
public class RenderServiceXSLFOImpl implements RenderService {

    private FopFactory fopFactory;
    private TransformerFactory transformerFactory;

    @Value("${adsbrecorder.report.fop.conf}")
    private String fopFactoryConfigurationFile;

    private StorageService storageService;
    private ReportJobRepository reportJobRepository;

    @Autowired
    public RenderServiceXSLFOImpl(StorageService storageService,
            ReportJobRepository reportJobRepository) {
        this.storageService = requireNonNull(storageService);
        this.reportJobRepository = requireNonNull(reportJobRepository);
    }

    @PostConstruct
    public void initFopFactory() {
        try {
            fopFactory = FopFactory.newInstance(new File(fopFactoryConfigurationFile));
            transformerFactory = new net.sf.saxon.TransformerFactoryImpl();
        } catch (SAXException | IOException e) {
            fopFactory = null;
        }
    }

    @Async
    @Override
    public String renderReport(ReportJob job) {
        File reportOutput = storageService.createReportOutputFile("pdf");
        job.setOutputName(reportOutput.getName());
        try {
            processXSLT(storageService.asInputStream(job.getDataFilename()),
                    storageService.asInputStream(job.getTemplateName()),
                    reportOutput);
            job.setProgress(100);
            this.reportJobRepository.save(job);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reportOutput.getAbsolutePath();
    }

    private void processXSLT(InputStream xml, InputStream xslt, File output) throws IOException {
        if (output.exists())
            output.delete();

        StreamSource source = new StreamSource(xml);
        StreamSource transformSource = new StreamSource(xslt);
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        try (OutputStream outStream = new FileOutputStream(output)) {
            Transformer xslfoTransformer = transformerFactory.newTransformer(transformSource);
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outStream);
            Result res = new SAXResult(fop.getDefaultHandler());
            xslfoTransformer.transform(source, res);
            outStream.flush();
        } catch (SAXException | TransformerException e) {
            e.printStackTrace();
        }
    }
}
