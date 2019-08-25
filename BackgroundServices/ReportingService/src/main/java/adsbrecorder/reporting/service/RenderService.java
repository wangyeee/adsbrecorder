package adsbrecorder.reporting.service;

import adsbrecorder.reporting.entity.ReportJob;

public interface RenderService {

    String renderReport(ReportJob job);
}
