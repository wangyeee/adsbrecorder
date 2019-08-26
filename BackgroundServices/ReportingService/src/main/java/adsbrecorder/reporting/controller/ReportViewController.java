package adsbrecorder.reporting.controller;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.common.aop.CheckOwnership;
import adsbrecorder.common.aop.LoginUser;
import adsbrecorder.common.aop.RequireLogin;
import adsbrecorder.common.aop.RequireOwnership;
import adsbrecorder.reporting.ReportingServiceMappings;
import adsbrecorder.reporting.entity.ReportJob;
import adsbrecorder.reporting.service.ReportService;
import adsbrecorder.reporting.service.StorageService;
import adsbrecorder.reporting.service.impl.ReportJobOwnershipValidator;
import adsbrecorder.user.entity.User;

@RestController
public class ReportViewController implements ReportingServiceMappings {

    private ReportService reportService;
    private StorageService storageService;

    @Autowired
    public ReportViewController(ReportService reportService,
            StorageService storageService) {
        this.reportService = requireNonNull(reportService);
        this.storageService = requireNonNull(storageService);
    }

    @RequireLogin
    @GetMapping(VIEW_RECENT_REPORT_JOBS)
    public ResponseEntity<Object> recentReportJobs(@RequestParam(name = "n", required = false, defaultValue = "5") int amount,
            @LoginUser User user) {
        if (amount < 1) amount = 5;
        List<ReportJob> reportJobs = reportService.getRecentReportJobs(user, amount);
        if (reportJobs.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No recent report job found",
                                 "user", user.getUsername()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(reportJobs);
    }

    @RequireOwnership
    @GetMapping(VIEW_REPORT_OUTPUT)
    public ResponseEntity<Object> viewReportOutput(@PathVariable(name = "id")
            @CheckOwnership(validator = ReportJobOwnershipValidator.class) BigInteger id) {
        ReportJob job = reportService.getById(id);
        try (InputStream in = storageService.asInputStream(job.getOutputName())) {
            byte[] rawReport = IOUtils.toByteArray(in);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
            headers.setCacheControl(CacheControl.noCache().getHeaderValue());
            return ResponseEntity.status(HttpStatus.OK).body(rawReport);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Report output not found",
                                 "report", job.getName()));
        }
    }
}
