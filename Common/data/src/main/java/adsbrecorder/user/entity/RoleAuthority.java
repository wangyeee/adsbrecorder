package adsbrecorder.user.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "AREC_ROLE_AUTH")
public class RoleAuthority implements Serializable {
    private static final long serialVersionUID = -3128535466070428381L;

    @Id
    @GeneratedValue
    @Column(name = "RA_ID")
    private Long roleAuthorityId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "RA_ROLE", updatable = false)
    private Role role;

    @ManyToOne(optional = false)
    @JoinColumn(name = "RA_AUTHORITY", updatable = false)
    private Authority authority;

    @Column(name = "RA_DESCRIPTION")
    private String description;

    public RoleAuthority() {
    }

    public Long getRoleAuthorityId() {
        return roleAuthorityId;
    }

    public void setRoleAuthorityId(Long roleAuthorityId) {
        this.roleAuthorityId = roleAuthorityId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public Authority getAuthority() {
        return this.authority;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((authority == null) ? 0 : authority.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((role == null) ? 0 : role.hashCode());
        result = prime * result + ((roleAuthorityId == null) ? 0 : roleAuthorityId.hashCode());
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
        RoleAuthority other = (RoleAuthority) obj;
        if (authority == null) {
            if (other.authority != null)
                return false;
        } else if (!authority.equals(other.authority))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (role == null) {
            if (other.role != null)
                return false;
        } else if (!role.equals(other.role))
            return false;
        if (roleAuthorityId == null) {
            if (other.roleAuthorityId != null)
                return false;
        } else if (!roleAuthorityId.equals(other.roleAuthorityId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RoleAuthority [roleAuthorityId=" + roleAuthorityId + ", role=" + role + ", authority=" + authority
                + ", description=" + description + "]";
    }
}
