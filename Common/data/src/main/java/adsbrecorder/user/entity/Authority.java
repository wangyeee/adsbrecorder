package adsbrecorder.user.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "AREC_AUTHORITY")
public class Authority implements GrantedAuthority {
    private static final long serialVersionUID = 7151637906017370714L;

    @Id
    @GeneratedValue
    @Column(name = "AUTHORITY_ID")
    private Long authorityId;

    @Column(name = "AUTHORITY_NAME", unique = true, nullable = false)
    private String authority;

    @Column(name = "AUTHORITY_DISP_NAME")
    private String displayName;

    @Column(name = "AUTHORITY_DESCRIPTION")
    private String description;

    public Authority() {
    }

    public Long getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((authority == null) ? 0 : authority.hashCode());
        result = prime * result + ((authorityId == null) ? 0 : authorityId.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
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
        Authority other = (Authority) obj;
        if (authority == null) {
            if (other.authority != null)
                return false;
        } else if (!authority.equals(other.authority))
            return false;
        if (authorityId == null) {
            if (other.authorityId != null)
                return false;
        } else if (!authorityId.equals(other.authorityId))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (displayName == null) {
            if (other.displayName != null)
                return false;
        } else if (!displayName.equals(other.displayName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Authority [authorityId=" + authorityId + ", authority=" + authority + ", displayName=" + displayName
                + ", description=" + description + "]";
    }
}
