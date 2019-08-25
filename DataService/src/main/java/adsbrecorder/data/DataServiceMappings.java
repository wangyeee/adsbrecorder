package adsbrecorder.data;

public interface DataServiceMappings {

    String READ_RECORD = "/api/data/record/{id}";
    String GET_TRACK = "/api/data/flight/{flight}/track";
    String READ_FLIGHT_RECORD = "/api/data/flight/{flight}";
    String READ_FLIGHT_RECORD_EVENT = "/api/data/flight/{flight}/latest";
    String READ_RECENT_FLIGHT = "/api/data/flight/recent";
    String READ_GM_KEY = "/api/data/map/init";
}
