package adsbrecorder.reporting.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import adsbrecorder.reporting.entity.ReportJob;
import adsbrecorder.user.entity.User;

public interface ReportService {

    String SIMPLE_DAILY_SUMMARY_REPORT_TYPE = "SIMPLE_DAILY_SUMMARY_REPORT";

    ReportJob runSimpleDailySummaryReport(String reportName, String reportType, Map<String, Object> parameters, User submittedBy);
    ReportJob getById(BigInteger id, User owner);
    ReportJob getById(BigInteger id);
    List<ReportJob> getRecentReportJobs(User owner, int amount);
    boolean reportNameExists(String name, User owner);
    List<ReportJob> searchReports(User owner, String reportType, String reportName, Map<String, String[]> params, int page0, int amount, long[] allMatchCount);
}
