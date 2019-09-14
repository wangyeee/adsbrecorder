package adsbrecorder.reporting;

public interface ReportingServiceMappings {

    String SIMPLE_DAILY_SUMMARY_REPORT = "/api/report/simpledailysummary";
    String GET_REPORT_PROGRESS = "/api/report/{id}/progress";
    String VIEW_REPORT_OUTPUT = "/api/report/{id}/view";
    String VIEW_RECENT_REPORT_JOBS = "/api/report/recent";
    String SEARCH_REPORT_JOBS = "/api/report/search";
    String CHECK_REPORT_NAME = "/api/report/namecheck";
}
