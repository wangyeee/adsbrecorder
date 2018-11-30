package adsbrecorder.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import adsbrecorder.jni.Aircraft;

@Entity
@Table(name = "AREC_RECORD")
public class TrackingRecord implements Serializable {
    private static final long serialVersionUID = -4440639705050275014L;

    public final static long TIME_INTERVAL = 1000L;  // 1 seconds

    @Id
    @GeneratedValue
    @Column(name = "RECORD_ID")
    private Long recordID;

    @Column(name = "RECORD_ADDR_ICAO")
    private int addressICAO;

    @ManyToOne(optional = false)
    @JoinColumn(name = "RECORD_FLIGHT")
    private Flight flight;

    @Column(name = "RECORD_LATITUDE")
    private double latitude;

    @Column(name = "RECORD_LOGITUDE")
    private double longitude;

    @Column(name = "RECORD_ALTITUDE")
    private int altitude;

    @Column(name = "RECORD_VELOCITY")
    private int velocity;

    @Column(name = "RECORD_HEADING")
    private int heading;

    @Column(name = "RECORD_LAST_TIME_SEEN")
    private long lastTimeSeen;

    @Column(name = "RECORD_MESSAGE_CTR")
    private long messageCounter;

    @Column(name = "RECORD_ODD_CPR_LAT")
    private int oddCprlat;

    @Column(name = "RECORD_ODD_CPR_LON")
    private int oddCprlon;

    @Column(name = "RECORD_EVEN_CPR_LAT")
    private int evenCprlat;

    @Column(name = "RECORD_EVEN_CPR_LON")
    private int evenCprlon;

    @Column(name = "RECORD_ODD_CPR_TIME")
    private long oddCprtime;

    @Column(name = "RECORD_EVEN_CPR_TIME")
    private long evenCprtime;

    @Column(name = "RECORD_DATE")
    private Date recordDate;

    public static final TrackingRecord emptyRecord() {
        TrackingRecord e = new TrackingRecord();
        e.recordDate = null;
        e.recordID = -1L;
        return e;
    }

    public TrackingRecord() {
        recordDate = new Date();
    }

    public TrackingRecord(Aircraft a, long time) {
        this(a);
        recordDate = new Date(time);
    }

    public TrackingRecord(Aircraft a) {
        this();
        flight = new Flight();
        flight.setFlightNumber(a.getFlightNumber());
        addressICAO = a.getAddressICAO();
        latitude = a.getLatitude();
        longitude = a.getLongitude();
        altitude = a.getAltitude();
        velocity = a.getVelocity();
        heading = a.getHeading();
        lastTimeSeen = a.getLastTimeSeen();
        messageCounter = a.getMessageCounter();
        oddCprlat = a.getOddCprlat();
        oddCprlon = a.getOddCprlon();
        evenCprlat = a.getEvenCprlat();
        evenCprlon = a.getEvenCprlon();
        oddCprtime = a.getOddCprtime();
        evenCprtime = a.getEvenCprtime();
    }

    public boolean tooClose(TrackingRecord another) {
        try {
            if (another == null)
                return false;
            if (!flight.getFlightNumber().equals(another.getFlight().getFlightNumber()))
                return false;
            long intval = Math.abs(this.recordDate.getTime() - another.recordDate.getTime());
            // TODO check distance?
            return TIME_INTERVAL > intval;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public Long getRecordID() {
        return recordID;
    }

    public void setRecordID(Long recordID) {
        this.recordID = recordID;
    }

    public int getAddressICAO() {
        return addressICAO;
    }

    public void setAddressICAO(int addressICAO) {
        this.addressICAO = addressICAO;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public long getLastTimeSeen() {
        return lastTimeSeen;
    }

    public void setLastTimeSeen(long lastTimeSeen) {
        this.lastTimeSeen = lastTimeSeen;
    }

    @JsonIgnore
    public long getMessageCounter() {
        return messageCounter;
    }

    public void setMessageCounter(long messageCounter) {
        this.messageCounter = messageCounter;
    }

    public int getOddCprlat() {
        return oddCprlat;
    }

    public void setOddCprlat(int oddCprlat) {
        this.oddCprlat = oddCprlat;
    }

    public int getOddCprlon() {
        return oddCprlon;
    }

    public void setOddCprlon(int oddCprlon) {
        this.oddCprlon = oddCprlon;
    }

    public int getEvenCprlat() {
        return evenCprlat;
    }

    public void setEvenCprlat(int evenCprlat) {
        this.evenCprlat = evenCprlat;
    }

    public int getEvenCprlon() {
        return evenCprlon;
    }

    public void setEvenCprlon(int evenCprlon) {
        this.evenCprlon = evenCprlon;
    }

    public long getOddCprtime() {
        return oddCprtime;
    }

    public void setOddCprtime(long oddCprtime) {
        this.oddCprtime = oddCprtime;
    }

    public long getEvenCprtime() {
        return evenCprtime;
    }

    public void setEvenCprtime(long evenCprtime) {
        this.evenCprtime = evenCprtime;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public long calculateLastSeenPeriod() {
        return System.currentTimeMillis() - recordDate.getTime();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + addressICAO;
        result = prime * result + altitude;
        result = prime * result + evenCprlat;
        result = prime * result + evenCprlon;
        result = prime * result + (int) (evenCprtime ^ (evenCprtime >>> 32));
        result = prime * result + ((flight == null) ? 0 : flight.hashCode());
        result = prime * result + heading;
        result = prime * result + (int) (lastTimeSeen ^ (lastTimeSeen >>> 32));
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (int) (messageCounter ^ (messageCounter >>> 32));
        result = prime * result + oddCprlat;
        result = prime * result + oddCprlon;
        result = prime * result + (int) (oddCprtime ^ (oddCprtime >>> 32));
        result = prime * result + ((recordDate == null) ? 0 : recordDate.hashCode());
        result = prime * result + ((recordID == null) ? 0 : recordID.hashCode());
        result = prime * result + velocity;
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TrackingRecord other = (TrackingRecord) obj;
        if (addressICAO != other.addressICAO)
            return false;
        if (altitude != other.altitude)
            return false;
        if (evenCprlat != other.evenCprlat)
            return false;
        if (evenCprlon != other.evenCprlon)
            return false;
        if (evenCprtime != other.evenCprtime)
            return false;
        if (flight == null) {
            if (other.flight != null)
                return false;
        } else if (!flight.equals(other.flight))
            return false;
        if (heading != other.heading)
            return false;
        if (lastTimeSeen != other.lastTimeSeen)
            return false;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
            return false;
        if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
            return false;
        if (messageCounter != other.messageCounter)
            return false;
        if (oddCprlat != other.oddCprlat)
            return false;
        if (oddCprlon != other.oddCprlon)
            return false;
        if (oddCprtime != other.oddCprtime)
            return false;
        if (recordDate == null) {
            if (other.recordDate != null)
                return false;
        } else if (!recordDate.equals(other.recordDate))
            return false;
        if (recordID == null) {
            if (other.recordID != null)
                return false;
        } else if (!recordID.equals(other.recordID))
            return false;
        if (velocity != other.velocity)
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TrackingRecord [recordID=" + recordID + ", addressICAO=" + addressICAO + ", flight=" + flight
                + ", latitude=" + latitude + ", longitude=" + longitude + ", altitude=" + altitude + ", velocity="
                + velocity + ", heading=" + heading + ", lastTimeSeen=" + lastTimeSeen + ", messageCounter="
                + messageCounter + ", oddCprlat=" + oddCprlat + ", oddCprlon=" + oddCprlon + ", evenCprlat="
                + evenCprlat + ", evenCprlon=" + evenCprlon + ", oddCprtime=" + oddCprtime + ", evenCprtime="
                + evenCprtime + ", recordDate=" + recordDate + "]";
    }
}
