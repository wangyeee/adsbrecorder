package adsbrecorder.user.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.fasterxml.jackson.annotation.JsonIgnore;

import adsbrecorder.common.auth.AuthorityObject;
import adsbrecorder.common.utils.AutoResolvableEntity;

@Entity
@Table(name = "AREC_USER")
public class User implements Serializable, AuthorityObject, AutoResolvableEntity {
    private static final long serialVersionUID = -8503282652540269258L;

    private final static User unauthorizedUser;

    static {
        unauthorizedUser = new User() {
            private static final long serialVersionUID = 4320147820427092797L;
            @Override
            public UsernamePasswordAuthenticationToken toAuthenticationToken() {
                return new UsernamePasswordAuthenticationToken(null, null);
            }
            @Override
            public Long getUserId() {
                return -1L;
            }
        };
    }

    @Id
    @GeneratedValue
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "USER_NAME", unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @Column(name = "USER_PASSWORD", nullable = false)
    private String password;

    @JsonIgnore
    @Column(name = "USER_SALT", nullable = false)
    private String salt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "USER_CREATION_DATE", nullable = false)
    private Date creationDate;

    private transient Set<UserRole> userRoles;

    private transient Set<Role> roles;

    private transient Set<Authority> authorities;

    public User() {
    }

    public static User getUnauthorizedUser() {
        return unauthorizedUser;
    }

    @Override
    public UsernamePasswordAuthenticationToken toAuthenticationToken() {
        return new UsernamePasswordAuthenticationToken(username, password, authorities);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
        if (userRoles != null && userRoles.isEmpty() == false) {
            Set<Role> roles = userRoles.stream().map(ur -> {
                ur.setUser(null);  // avoid infinite loop when generating json
                return ur.getRole();
            }).collect(Collectors.toSet());
            this.setRoles(roles);
        }
    }

    @JsonIgnore
    public Set<Role> getRoles() {
        return roles;
    }

    private void setRoles(Set<Role> roles) {
        this.roles = roles;
        if (roles != null && roles.isEmpty() == false) {
            if (this.authorities == null) {
                this.authorities = new HashSet<Authority>();
            } else {
                this.authorities.clear();
            }
            roles.forEach(role -> {
                Set<Authority> newAuth = role.getAuthorities();
                if (newAuth != null && newAuth.isEmpty() == false) {
                    this.authorities.addAll(role.getAuthorities());
                }
            });
        }
    }

    @JsonIgnore
    public Set<Authority> getAuthorities() {
        return authorities;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((salt == null) ? 0 : salt.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
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
        User other = (User) obj;
        if (creationDate == null) {
            if (other.creationDate != null)
                return false;
        } else if (!creationDate.equals(other.creationDate))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (salt == null) {
            if (other.salt != null)
                return false;
        } else if (!salt.equals(other.salt))
            return false;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", username=" + username + ", password=" + password + ", salt=" + salt
                + ", creationDate=" + creationDate + ", roles=" + roles + "]";
    }

    @Override
    public boolean isValidEntity() {
        return this.getUserId() > 0L;
    }
}
