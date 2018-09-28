package adsbrecorder.entity;

import java.io.Serializable;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "AREC_FLIGHT")
public class Flight implements Serializable {
    private static final long serialVersionUID = -4797684006069839223L;
    private static final String REGEX = "^[A-Z\\d]{2}[A-Z]?\\d{1,4}[A-Z]?$";

    public final static boolean isFlightNumber(String flightNumber) {
        return Pattern.matches(REGEX, flightNumber);
    }

    @Id
    @GeneratedValue
    @Column(name = "FLIGHT_ID")
    private Long flightID;

    @Column(unique = true, name = "FLIGHT_NUMBER")
    private String flightNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "FLIGHT_AIRLINE")
    private Airline airline;

    public Flight() {
    }

    public Long getFlightID() {
        return flightID;
    }

    public void setFlightID(Long flightID) {
        this.flightID = flightID;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((airline == null) ? 0 : airline.hashCode());
        result = prime * result + ((flightID == null) ? 0 : flightID.hashCode());
        result = prime * result + ((flightNumber == null) ? 0 : flightNumber.hashCode());
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
        Flight other = (Flight) obj;
        if (airline == null) {
            if (other.airline != null)
                return false;
        } else if (!airline.equals(other.airline))
            return false;
        if (flightID == null) {
            if (other.flightID != null)
                return false;
        } else if (!flightID.equals(other.flightID))
            return false;
        if (flightNumber == null) {
            if (other.flightNumber != null)
                return false;
        } else if (!flightNumber.equals(other.flightNumber))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Flight [flightID=" + flightID + ", flightNumber=" + flightNumber + ", airline=" + airline + "]";
    }
}
