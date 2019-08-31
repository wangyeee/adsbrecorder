package adsbrecorder.reporting.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import adsbrecorder.reporting.entity.ReportJob;
import adsbrecorder.user.entity.User;

public interface ReportProcess {

    String name();
    void run(ReportJob job);
    List<ReportJob> search(User owner, String reportName, Map<String, String[]> params,
            int page0, int amount, long[] allMatchCount);

    default Date formatDate(String date, String format, long defaultMillis) {
        Date date0 = null;
        try {
            DateFormat df = new SimpleDateFormat(format);
            date0 = df.parse(date);
        } catch (Exception e) {
            date0 = new Date(defaultMillis);
        }
        return date0;
    }

    Date START_DATE_LIMIT = new Date(0L);
    Date END_DATE_LIMIT = new Date(253402167600000L);  // 9999-12-31
}
