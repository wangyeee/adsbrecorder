package adsbrecorder.receiver.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Id;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import adsbrecorder.client.entity.RemoteReceiver;
import adsbrecorder.common.utils.GeoCoordinate;

@Document
public class TrackingRecord implements Serializable, GeoCoordinate {
    private static final long serialVersionUID = 3961029343020762424L;

    @Id
    private BigInteger id;

    private int addressICAO;

    private String flight;

    private double latitude;

    private double longitude;

    private int altitude;

    private int velocity;

    private int heading;

    private int verticalRate;

    private long lastTimeSeen;

    private Date recordDate;

    @Transient
    private transient RemoteReceiver sourceReceiver;

    private Long sourceReceiverID;

    public TrackingRecord() {
    }

    public String toXML() {
        StringBuilder sb = new StringBuilder("<TRACKING_RECORD>");
        {
            sb.append("<ID>");sb.append(StringEscapeUtils.escapeXml(this.id.toString()));sb.append("</ID>");
            sb.append("<ICAO>");sb.append(this.addressICAO);sb.append("</ICAO>");
            sb.append("<flight>");sb.append(this.flight);sb.append("</flight>");
            sb.append("<latitude>");sb.append(this.latitude);sb.append("</latitude>");
            sb.append("<longitude>");sb.append(this.longitude);sb.append("</longitude>");
            sb.append("<altitude>");sb.append(this.altitude);sb.append("</altitude>");
            sb.append("<velocity>");sb.append(this.velocity);sb.append("</velocity>");
            sb.append("<heading>");sb.append(this.heading);sb.append("</heading>");
            sb.append("<verticalRate>");sb.append(this.verticalRate);sb.append("</verticalRate>");
            sb.append("<lastTimeSeen>");sb.append(this.lastTimeSeen);sb.append("</lastTimeSeen>");
            sb.append("<recordDate>");sb.append(StringEscapeUtils.escapeXml(this.recordDate.toString()));sb.append("</recordDate>");
            sb.append("<sourceReceiverID>");sb.append(this.sourceReceiverID);sb.append("</sourceReceiverID>");
        }
        sb.append("</TRACKING_RECORD>");
        return sb.toString();
    }

    @JsonIgnore
    public VelocityUpdate getVelocityUpdate() {
        if (Math.abs(latitude) > 0.0 && Math.abs(longitude) > 0.0)
            return null;
        VelocityUpdate vu = new VelocityUpdate();
        vu.setAddressICAO(addressICAO);
        vu.setApplied(false);
        vu.setHeading(heading);
        vu.setLastTimeSeen(lastTimeSeen);
        vu.setRecordDate(recordDate);
        vu.setSourceReceiver(sourceReceiver);
        vu.setVelocity(velocity);
        vu.setVerticalRate(verticalRate);
        return vu;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public int getAddressICAO() {
        return addressICAO;
    }

    public void setAddressICAO(int addressICAO) {
        this.addressICAO = addressICAO;
    }

    public String getFlight() {
        return flight;
    }

    public void setFlight(String flight) {
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

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    @JsonIgnore
    public RemoteReceiver getSourceReceiver() {
        return sourceReceiver;
    }

    public void setSourceReceiver(RemoteReceiver sourceReceiver) {
        this.sourceReceiverID = sourceReceiver == null ? -1L : sourceReceiver.getRemoteReceiverID();
        this.sourceReceiver = sourceReceiver;
    }

    public Long getSourceReceiverID() {
        return sourceReceiverID;
    }

    public int getVerticalRate() {
        return verticalRate;
    }

    public void setVerticalRate(int verticalRate) {
        this.verticalRate = verticalRate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + addressICAO;
        result = prime * result + altitude;
        result = prime * result + ((flight == null) ? 0 : flight.hashCode());
        result = prime * result + heading;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + (int) (lastTimeSeen ^ (lastTimeSeen >>> 32));
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((recordDate == null) ? 0 : recordDate.hashCode());
        result = prime * result + ((sourceReceiverID == null) ? 0 : sourceReceiverID.hashCode());
        result = prime * result + velocity;
        result = prime * result + verticalRate;
        return result;
    }

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
        if (flight == null) {
            if (other.flight != null)
                return false;
        } else if (!flight.equals(other.flight))
            return false;
        if (heading != other.heading)
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (lastTimeSeen != other.lastTimeSeen)
            return false;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
            return false;
        if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
            return false;
        if (recordDate == null) {
            if (other.recordDate != null)
                return false;
        } else if (!recordDate.equals(other.recordDate))
            return false;
        if (sourceReceiverID == null) {
            if (other.sourceReceiverID != null)
                return false;
        } else if (!sourceReceiverID.equals(other.sourceReceiverID))
            return false;
        if (velocity != other.velocity)
            return false;
        if (verticalRate != other.verticalRate)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TrackingRecord [id=" + id + ", addressICAO=" + addressICAO + ", flight=" + flight + ", latitude="
                + latitude + ", longitude=" + longitude + ", altitude=" + altitude + ", velocity=" + velocity
                + ", heading=" + heading + ", verticalRate=" + verticalRate + ", lastTimeSeen=" + lastTimeSeen
                + ", recordDate=" + recordDate + ", sourceReceiverID=" + sourceReceiverID + "]";
    }
}
