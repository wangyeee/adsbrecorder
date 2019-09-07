package adsbrecorder.reporting.controller;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.common.aop.annotation.CheckOwnership;
import adsbrecorder.common.aop.annotation.LoginUser;
import adsbrecorder.common.aop.annotation.RequireLogin;
import adsbrecorder.common.aop.annotation.RequireOwnership;
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
    @GetMapping(SEARCH_REPORT_JOBS)
    public ResponseEntity<Object> searchReportJobs(
            @RequestParam(name = "report", required = false, defaultValue = "") String reportType,
            @RequestParam(name = "name", required = false, defaultValue = "") String reportName,
            @RequestParam(name = "p", required = false, defaultValue = "1") int page,
            @RequestParam(name = "n", required = false, defaultValue = "5") int amount,
            @LoginUser User user, HttpServletRequest request) {
        if (page <= 0) page = -page;
        if (page > 0) page--;
        if (amount < 0) amount = -amount;
        if (amount == 0) amount = 5;
        long[] count = new long[1];
        List<ReportJob> reports = this.reportService.searchReports(user, reportType, reportName,
                request.getParameterMap(), page, amount, count);
        if (reports.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No report found",
                                 "user", user.getUsername()));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("reports", reports,
                             "totalCount", count[0]));
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
    @GetMapping(value = VIEW_REPORT_OUTPUT, produces = {"application/pdf"})
    public @ResponseBody ResponseEntity<Object> viewReportOutput(@PathVariable(name = "id")
           @CheckOwnership(validator = ReportJobOwnershipValidator.class) BigInteger id) {
        ReportJob job = reportService.getById(id);
        try (InputStream in = storageService.asInputStream(job.getOutputName())) {
            byte[] rawReport = IOUtils.toByteArray(in);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
            headers.add("content-disposition", String.format("attachment;filename=%s.pdf", job.getName()));
            headers.setCacheControl(CacheControl.noCache().getHeaderValue());
            return ResponseEntity.status(HttpStatus.OK).body(rawReport);
        } catch (IOException | RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Report output not found",
                                 "report", job.getName()));
        }
    }
}
