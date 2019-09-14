package adsbrecorder.receiver.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Id;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import adsbrecorder.client.entity.RemoteReceiver;

@Document
public class VelocityUpdate implements Serializable {
    private static final long serialVersionUID = -7539186606216471729L;

    @Id
    private BigInteger id;

    private int addressICAO;

    private int velocity;

    private int heading;

    private int verticalRate;

    private long lastTimeSeen;

    private Date recordDate;

    @Transient
    private transient RemoteReceiver sourceReceiver;

    private Long sourceReceiverID;

    private boolean applied;

    public VelocityUpdate() {
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

    public int getVerticalRate() {
        return verticalRate;
    }

    public void setVerticalRate(int verticalRate) {
        this.verticalRate = verticalRate;
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
        this.sourceReceiverID = sourceReceiver == null ? null : sourceReceiver.getRemoteReceiverID();
        this.sourceReceiver = sourceReceiver;
    }

    public Long getSourceReceiverID() {
        return sourceReceiverID;
    }

    public boolean isApplied() {
        return applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + addressICAO;
        result = prime * result + (applied ? 1231 : 1237);
        result = prime * result + heading;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + (int) (lastTimeSeen ^ (lastTimeSeen >>> 32));
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
        VelocityUpdate other = (VelocityUpdate) obj;
        if (addressICAO != other.addressICAO)
            return false;
        if (applied != other.applied)
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
        return "VelocityUpdate [id=" + id + ", addressICAO=" + addressICAO + ", velocity=" + velocity + ", heading="
                + heading + ", verticalRate=" + verticalRate + ", lastTimeSeen=" + lastTimeSeen + ", recordDate="
                + recordDate + ", sourceReceiverID=" + sourceReceiverID + ", applied=" + applied + "]";
    }
}
