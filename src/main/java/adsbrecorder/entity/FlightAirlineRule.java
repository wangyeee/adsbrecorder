package adsbrecorder.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "AREC_FA_RULE")
public class FlightAirlineRule implements Serializable {
    private static final long serialVersionUID = -7917355147083957386L;

    @Id
    @GeneratedValue
    @Column(name = "RULE_ID")
    private Long ruleID;

    @Column(name = "RULE_FLIGHT_NUMBER", nullable = false, unique = true)
    private String flightNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "RULE_AIRLINE")
    private Airline airline;

    // reserved
    @Column(name = "RULE_TYPE")
    private int ruleType;

    public FlightAirlineRule() {
    }

    public Long getRuleID() {
        return ruleID;
    }

    public void setRuleID(Long ruleID) {
        this.ruleID = ruleID;
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

    public int getRuleType() {
        return ruleType;
    }

    public void setRuleType(int ruleType) {
        this.ruleType = ruleType;
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
        result = prime * result + ((flightNumber == null) ? 0 : flightNumber.hashCode());
        result = prime * result + ((ruleID == null) ? 0 : ruleID.hashCode());
        result = prime * result + ruleType;
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
        FlightAirlineRule other = (FlightAirlineRule) obj;
        if (airline == null) {
            if (other.airline != null)
                return false;
        } else if (!airline.equals(other.airline))
            return false;
        if (flightNumber == null) {
            if (other.flightNumber != null)
                return false;
        } else if (!flightNumber.equals(other.flightNumber))
            return false;
        if (ruleID == null) {
            if (other.ruleID != null)
                return false;
        } else if (!ruleID.equals(other.ruleID))
            return false;
        if (ruleType != other.ruleType)
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FlightAirlineRule [ruleID=" + ruleID + ", flightNumber=" + flightNumber + ", airline=" + airline
                + ", ruleType=" + ruleType + "]";
    }
}
