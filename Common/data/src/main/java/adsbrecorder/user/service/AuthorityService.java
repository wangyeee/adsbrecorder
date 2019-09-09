package adsbrecorder.user.service;

import java.util.Set;

import adsbrecorder.user.entity.Authority;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.entity.UserAuthority;

public interface AuthorityService {

    Authority findById(Long authorityId);
    Authority createAuthority(Authority authority);
    Set<UserAuthority> assignAuthoritiesToUser(User user, Set<Authority> authorities);
    Set<Authority> findAvailableAuthorities(User user);
}
