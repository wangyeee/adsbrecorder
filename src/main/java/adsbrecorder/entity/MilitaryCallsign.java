package adsbrecorder.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AREC_MIL_REC")
public class MilitaryCallsign implements Serializable {

    private static final long serialVersionUID = 1706226686049333987L;

    @Id
    @GeneratedValue
    @Column(name = "MIN_REC_ID")
    private Long militaryRecordID;

    @Column(name = "MIN_REC_CALLSIGN")
    private String callsign;

    @Column(name = "MIN_REC_TYPE")
    private String type;

    @Column(name = "MIN_REC_UNIT")
    private String unit;

    @Column(name = "MIN_REC_COUNTRY")
    private String country;

    public MilitaryCallsign() {
    }

    public static MilitaryCallsign emptyRecord() {
        return null;
    }

    public Long getMilitaryRecordID() {
        return militaryRecordID;
    }

    public void setMilitaryRecordID(Long militaryRecordID) {
        this.militaryRecordID = militaryRecordID;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((callsign == null) ? 0 : callsign.hashCode());
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result + ((militaryRecordID == null) ? 0 : militaryRecordID.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
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
        MilitaryCallsign other = (MilitaryCallsign) obj;
        if (callsign == null) {
            if (other.callsign != null)
                return false;
        } else if (!callsign.equals(other.callsign))
            return false;
        if (country == null) {
            if (other.country != null)
                return false;
        } else if (!country.equals(other.country))
            return false;
        if (militaryRecordID == null) {
            if (other.militaryRecordID != null)
                return false;
        } else if (!militaryRecordID.equals(other.militaryRecordID))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (unit == null) {
            if (other.unit != null)
                return false;
        } else if (!unit.equals(other.unit))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MilitaryRecords [militaryRecordID=" + militaryRecordID + ", callsign=" + callsign + ", type=" + type
                + ", unit=" + unit + ", country=" + country + "]";
    }
}
