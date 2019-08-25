package adsbrecorder.common.utils;

public interface GeoMathUtils {

    double FOOT_TO_METER = 0.3048;
    double EARTH_RADIUS = 6.3781E6;
    double DEG_TO_RAD = Math.PI / 180.0;

    /**
     * Calculate vertical speed between two tracking records
     * @param from first record
     * @param to second record
     * @return vertical speed in ft/min
     */
    default double calcVerticalRate(GeoCoordinate from, GeoCoordinate to) {
        int diff = to.getAltitude() - from.getAltitude();
        long ms = to.getLastTimeSeen() - from.getLastTimeSeen();
        return diff / (ms / 1000.0 / 60.0);
    }

    /**
     * Calculate bearing between two tracking records
     * @param from first record
     * @param to second record
     * @return heading in degree
     */
    default double calcHeading(GeoCoordinate from, GeoCoordinate to) {
        double x = Math.cos(DEG_TO_RAD * from.getLatitude()) *
                Math.sin(DEG_TO_RAD * to.getLatitude()) -
                Math.sin(DEG_TO_RAD * from.getLatitude()) *
                Math.cos(DEG_TO_RAD * to.getLatitude()) *
                Math.cos(DEG_TO_RAD * (to.getLongitude() - from.getLongitude()));
        double y = Math.sin(DEG_TO_RAD * (to.getLongitude() - from.getLongitude())) *
                Math.cos(DEG_TO_RAD * to.getLatitude());
        double heading = Math.atan2(y, x) / DEG_TO_RAD;
        return heading > 0.0 ? heading : heading + 360.0;
    }

    /**
     * Calculate ground speed between two tracking records
     * @param from first record
     * @param to second record
     * @return ground speed in meter/second
     */
    default double calcGroundSpeed(GeoCoordinate from, GeoCoordinate to) {
        double rAvg = EARTH_RADIUS + FOOT_TO_METER * (from.getAltitude() + to.getAltitude()) / 2.0;
        double x0 = rAvg * Math.cos(DEG_TO_RAD * from.getLatitude()) * Math.cos(DEG_TO_RAD * from.getLongitude());
        double y0 = rAvg * Math.cos(DEG_TO_RAD * from.getLatitude()) * Math.sin(DEG_TO_RAD * from.getLongitude());
        double x1 = rAvg * Math.cos(DEG_TO_RAD * to.getLatitude()) * Math.cos(DEG_TO_RAD * to.getLongitude());
        double y1 = rAvg * Math.cos(DEG_TO_RAD * to.getLatitude()) * Math.sin(DEG_TO_RAD * to.getLongitude());
        double dist = Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1));
        long ms = to.getLastTimeSeen() - from.getLastTimeSeen();
        return dist * 1000 / ms;
    }

    /**
     * Convert meter per second to knots
     * @param mps speed in meter per second
     * @return speed in knots
     */
    default double mpsToKnots(double mps) {
        return mps * 1.94384;
    }
}
