package adsbrecorder.receiver.repo;

import java.util.Date;
import java.util.Map;

public interface TrackingRecordDataRepository {
    Map<String, Date> recentFlights(int amount);
}
