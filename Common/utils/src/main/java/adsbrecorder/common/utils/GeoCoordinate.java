package adsbrecorder.common.utils;

public interface GeoCoordinate {
    double getLatitude();
    double getLongitude();
    int getAltitude();
    long getLastTimeSeen();
}
