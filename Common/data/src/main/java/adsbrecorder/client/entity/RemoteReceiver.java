package adsbrecorder.client.entity;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import adsbrecorder.common.auth.AuthorityObject;
import adsbrecorder.common.auth.ListOfAuthorities;
import adsbrecorder.user.entity.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "AREC_RMT_REC")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class RemoteReceiver implements Serializable, ListOfAuthorities, AuthorityObject {

    private static final long serialVersionUID = 4067048468428019795L;

    private final static RemoteReceiver dummy;

    static {
        dummy = new RemoteReceiver() {
            private static final long serialVersionUID = 1L;
            @Override
            public UsernamePasswordAuthenticationToken toAuthenticationToken() {
                return new UsernamePasswordAuthenticationToken(null, null);
            }
            @Override
            public User getOwner() {
                return User.getUnauthorizedUser();
            }
            @Override
            public Long getRemoteReceiverID() {
                return -1L;
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

    @Column(name = "RECEIVER_DESCRIPTION")
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "RECEIVER_OWNER")
    private User owner;

    @Override
    public UsernamePasswordAuthenticationToken toAuthenticationToken() {
        return new UsernamePasswordAuthenticationToken(remoteReceiverName, remoteReceiverKey, subSetAuthorities(owner.getAuthorities()));
    }

    private Collection<? extends GrantedAuthority> subSetAuthorities(Collection<? extends GrantedAuthority> full) {
        return full;
    }
}
