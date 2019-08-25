package adsbrecorder.reporting.service.impl;

import adsbrecorder.reporting.entity.ReportJob;

public interface ReportProcess {

    String name();
    void run(ReportJob job);
}
