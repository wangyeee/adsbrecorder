package adsbrecorder.reporting.service.impl;

import static java.util.Objects.requireNonNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import adsbrecorder.receiver.entity.TrackingRecord;
import adsbrecorder.receiver.repo.TrackingRecordRepository;
import adsbrecorder.reporting.entity.ReportJob;
import adsbrecorder.reporting.repo.ReportJobRepository;
import adsbrecorder.reporting.service.RenderService;
import adsbrecorder.reporting.service.ReportService;
import adsbrecorder.reporting.service.StorageService;

@Component("simpleDailySummaryReport")
public class SimpleDailySummaryReport implements ReportProcess {

    private TrackingRecordRepository trackingRecordRepository;
    private ReportJobRepository reportJobRepository;
    private StorageService storageService;
    private RenderService renderService;

    @Autowired
    public SimpleDailySummaryReport(TrackingRecordRepository trackingRecordRepository,
            ReportJobRepository reportJobRepository,
            StorageService storageService,
            RenderService renderService) {
        this.trackingRecordRepository = requireNonNull(trackingRecordRepository);
        this.reportJobRepository = requireNonNull(reportJobRepository);
        this.storageService = requireNonNull(storageService);
        this.renderService = requireNonNull(renderService);
    }

    @Override
    public final String name() {
        return ReportService.SIMPLE_DAILY_SUMMARY_REPORT_TYPE;
    }

    @Async
    @Override
    public void run(ReportJob currentJob) {
        Date reportDate = (Date) currentJob.getParameters().get("day");
        File dataOutput = storageService.createDataOutputFile("xml");
        currentJob.setDataFilename(dataOutput.getName());
        currentJob.setProgress(1);
        this.reportJobRepository.save(currentJob);
        List<TrackingRecord> allRecords = this.trackingRecordRepository.findAllOnDate(reportDate);
        try (BufferedWriter xmlWriter = new BufferedWriter(new FileWriter(dataOutput, Charset.forName("UTF-8")))) {
            String header = String.format("<%s><parameters><day>%s</day><submitted_by>%d</submitted_by></parameters>",
                    StringEscapeUtils.escapeXml(name()),
                    StringEscapeUtils.escapeXml(reportDate.toString()),
                    currentJob.getSubmittedByUserId());
            xmlWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            xmlWriter.newLine();
            xmlWriter.write(header);
            xmlWriter.newLine();
            xmlWriter.write("<records>");
            allRecords.forEach(record -> {
                try {
                    xmlWriter.write(record.toXML());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            xmlWriter.write("</records>");
            xmlWriter.newLine();
            xmlWriter.write(String.format("</%s>", StringEscapeUtils.escapeXml(name())));
            xmlWriter.flush();
            currentJob.setProgress(50);
            this.reportJobRepository.save(currentJob);
            this.renderService.renderReport(currentJob);
        } catch (IOException e) {
            e.printStackTrace();
            currentJob.setDataFilename(null);
            currentJob.setProgress(0);
        }
    }
}
