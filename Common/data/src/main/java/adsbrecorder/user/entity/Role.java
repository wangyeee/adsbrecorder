package adsbrecorder.user.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import adsbrecorder.common.utils.AutoResolvableEntity;

@Entity
@Table(name = "AREC_ROLE")
public class Role implements Serializable, AutoResolvableEntity {
    private static final long serialVersionUID = -4822539068194765879L;

    private static Role _invalidRole;

    static {
        _invalidRole = new Role() {
            private static final long serialVersionUID = 5146663304287179026L;
            @Override
            public Long getRoleId() {
                return -1L;
            }
        };
    }

    @Id
    @GeneratedValue
    @Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "ROLE_NAME", nullable = false, unique = true)
    private String roleName;

    @Column(name = "ROLE_DISPLAY_NAME")
    private String displayName;

    @Column(name = "ROLE_DESCRIPTION")
    private String description;

    private transient Set<Authority> authorities;

    public Role() {
    }

    public static Role invalidRole() {
        return _invalidRole;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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
    
    public Set<Authority> getAuthorities() {
        return authorities;
    }
    
    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
        result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
        result = prime * result + ((roleName == null) ? 0 : roleName.hashCode());
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
        Role other = (Role) obj;
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
        if (roleId == null) {
            if (other.roleId != null)
                return false;
        } else if (!roleId.equals(other.roleId))
            return false;
        if (roleName == null) {
            if (other.roleName != null)
                return false;
        } else if (!roleName.equals(other.roleName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Role [roleId=" + roleId + ", roleName=" + roleName + ", displayName=" + displayName + ", description="
                + description + "]";
    }

    @Override
    public boolean isValidEntity() {
        return this.getRoleId() > 0L;
    }
}
