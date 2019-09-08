package adsbrecorder.user.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "AREC_USER_AUTHORITY")
public class UserAuthority implements Serializable {
    private static final long serialVersionUID = -8650749669940559121L;

    @Id
    @GeneratedValue
    @Column(name = "UA_ID")
    private Long userAuthorityId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "UA_AUTHORITY", updatable = false)
    private Authority authority;

    @ManyToOne(optional = false)
    @JoinColumn(name = "UA_USER", updatable = false)
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UA_CREATION_DATE", nullable = false)
    private Date creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UA_EXPIRATION_DATE")
    private Date expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "UA_TYPE", nullable = false)
    private UserAuthorityType type;

    public UserAuthority() {
    }

    public Long getUserId() {
        return this.user == null ? -1L : this.user.getUserId();
    }

    public void setUserId(Long userId) {
    }

    public Long getUserAuthorityId() {
        return userAuthorityId;
    }

    public void setUserAuthorityId(Long userAuthorityId) {
        this.userAuthorityId = userAuthorityId;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public UserAuthorityType getType() {
        return type;
    }

    public void setType(UserAuthorityType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((authority == null) ? 0 : authority.hashCode());
        result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
        result = prime * result + ((expirationDate == null) ? 0 : expirationDate.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        result = prime * result + ((userAuthorityId == null) ? 0 : userAuthorityId.hashCode());
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
        UserAuthority other = (UserAuthority) obj;
        if (authority == null) {
            if (other.authority != null)
                return false;
        } else if (!authority.equals(other.authority))
            return false;
        if (creationDate == null) {
            if (other.creationDate != null)
                return false;
        } else if (!creationDate.equals(other.creationDate))
            return false;
        if (expirationDate == null) {
            if (other.expirationDate != null)
                return false;
        } else if (!expirationDate.equals(other.expirationDate))
            return false;
        if (type != other.type)
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        if (userAuthorityId == null) {
            if (other.userAuthorityId != null)
                return false;
        } else if (!userAuthorityId.equals(other.userAuthorityId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "UserAuthority [userAuthorityId=" + userAuthorityId + ", authority=" + authority + ", user=" + user
                + ", creationDate=" + creationDate + ", expirationDate=" + expirationDate + ", type=" + type + "]";
    }
}
