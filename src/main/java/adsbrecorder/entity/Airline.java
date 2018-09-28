package adsbrecorder.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AREC_AIRLINE")
public class Airline implements Serializable {
    private static final long serialVersionUID = 4265318302125513548L;

    @Id
    @GeneratedValue
    @Column(name = "AIRLINE_ID")
    private Long airlineID;

    @Column(name = "AIRLINE_IATA")
    private String IATA;

    @Column(name = "AIRLINE_ICAO")
    private String ICAO;

    @Column(name = "AIRLINE_NAME")
    private String name;

    @Column(name = "AIRLINE_CALLSIGN")
    private String callSign;

    @Column(name = "AIRLINE_COUNTRY")
    private String country;

    @Column(name = "AIRLINE_COMMENTS")
    private String comments;

    public Airline() {
    }

    public Long getAirlineID() {
        return airlineID;
    }

    public void setAirlineID(Long airlineID) {
        this.airlineID = airlineID;
    }

    public String getIATA() {
        return IATA;
    }

    public void setIATA(String iATA) {
        IATA = iATA;
    }

    public String getICAO() {
        return ICAO;
    }

    public void setICAO(String iCAO) {
        ICAO = iCAO;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((IATA == null) ? 0 : IATA.hashCode());
        result = prime * result + ((ICAO == null) ? 0 : ICAO.hashCode());
        result = prime * result + ((airlineID == null) ? 0 : airlineID.hashCode());
        result = prime * result + ((callSign == null) ? 0 : callSign.hashCode());
        result = prime * result + ((comments == null) ? 0 : comments.hashCode());
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Airline other = (Airline) obj;
        if (IATA == null) {
            if (other.IATA != null)
                return false;
        } else if (!IATA.equals(other.IATA))
            return false;
        if (ICAO == null) {
            if (other.ICAO != null)
                return false;
        } else if (!ICAO.equals(other.ICAO))
            return false;
        if (airlineID == null) {
            if (other.airlineID != null)
                return false;
        } else if (!airlineID.equals(other.airlineID))
            return false;
        if (callSign == null) {
            if (other.callSign != null)
                return false;
        } else if (!callSign.equals(other.callSign))
            return false;
        if (comments == null) {
            if (other.comments != null)
                return false;
        } else if (!comments.equals(other.comments))
            return false;
        if (country == null) {
            if (other.country != null)
                return false;
        } else if (!country.equals(other.country))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Airline [airlineID=" + airlineID + ", IATA=" + IATA + ", ICAO=" + ICAO + ", name=" + name
                + ", callSign=" + callSign + ", country=" + country + ", comments=" + comments + "]";
    }
}
