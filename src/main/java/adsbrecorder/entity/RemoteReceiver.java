package adsbrecorder.entity;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity
@Table(name = "AREC_RMT_REC")
public class RemoteReceiver implements Serializable {

    private static final long serialVersionUID = 4067048468428019795L;

    private final static RemoteReceiver dummy;

    static {
        dummy = new RemoteReceiver() {
            private static final long serialVersionUID = 1L;
            @Override
            public UsernamePasswordAuthenticationToken toAuthenticationToken() {
                return new UsernamePasswordAuthenticationToken(null, null);
            }
        };
    }

    public final static RemoteReceiver unAuthorizedReceiver() {
        return dummy;
    }

    @Id
    @GeneratedValue
    @Column(name = "RECEIVER_ID")
    private Long remoteReceiverID;

    @Column(name = "RECEIVER_NAME")
    private String remoteReceiverName;

    @Column(name = "RECEIVER_KEY")
    private String remoteReceiverKey;

    public RemoteReceiver() {
    }

    public UsernamePasswordAuthenticationToken toAuthenticationToken() {
        return new UsernamePasswordAuthenticationToken(remoteReceiverName, remoteReceiverKey, Arrays.asList(new SimpleGrantedAuthority("ADD_TRACKING_RECORD")));
    }

    public Long getRemoteReceiverID() {
        return remoteReceiverID;
    }

    public void setRemoteReceiverID(Long remoteReceiverID) {
        this.remoteReceiverID = remoteReceiverID;
    }

    public String getRemoteReceiverName() {
        return remoteReceiverName;
    }

    public void setRemoteReceiverName(String remoteReceiverName) {
        this.remoteReceiverName = remoteReceiverName;
    }

    public String getRemoteReceiverKey() {
        return remoteReceiverKey;
    }

    public void setRemoteReceiverKey(String remoteReceiverKey) {
        this.remoteReceiverKey = remoteReceiverKey;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((remoteReceiverID == null) ? 0 : remoteReceiverID.hashCode());
        result = prime * result + ((remoteReceiverKey == null) ? 0 : remoteReceiverKey.hashCode());
        result = prime * result + ((remoteReceiverName == null) ? 0 : remoteReceiverName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        RemoteReceiver other = (RemoteReceiver) obj;
        if (remoteReceiverID == null) {
            if (other.remoteReceiverID != null)
                return false;
        } else if (!remoteReceiverID.equals(other.remoteReceiverID))
            return false;
        if (remoteReceiverKey == null) {
            if (other.remoteReceiverKey != null)
                return false;
        } else if (!remoteReceiverKey.equals(other.remoteReceiverKey))
            return false;
        if (remoteReceiverName == null) {
            if (other.remoteReceiverName != null)
                return false;
        } else if (!remoteReceiverName.equals(other.remoteReceiverName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RemoteReceiver [remoteReceiverID=" + remoteReceiverID + ", remoteReceiverName=" + remoteReceiverName
                + ", remoteReceiverKey=" + remoteReceiverKey + "]";
    }
}
