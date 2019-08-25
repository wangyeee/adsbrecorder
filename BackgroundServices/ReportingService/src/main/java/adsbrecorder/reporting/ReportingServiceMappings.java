package adsbrecorder.reporting;

public interface ReportingServiceMappings {

    String SIMPLE_DAILY_SUMMARY_REPORT = "/api/report/simpledailysummary";
    String GET_REPORT_PROGRESS = "/api/report/{id}/progress";
    String VIEW_REPORT_OUTPUT = "/api/report/{id}/view";
}
