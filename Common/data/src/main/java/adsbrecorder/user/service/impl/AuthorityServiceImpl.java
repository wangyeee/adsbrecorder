package adsbrecorder.user.service.impl;

import static java.util.Objects.requireNonNull;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adsbrecorder.user.entity.Authority;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.entity.UserAuthority;
import adsbrecorder.user.entity.UserAuthorityType;
import adsbrecorder.user.repo.AuthorityRepository;
import adsbrecorder.user.repo.UserAuthorityRepository;
import adsbrecorder.user.service.AuthorityService;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private AuthorityRepository authorityRepository;
    private UserAuthorityRepository userAuthorityRepository;

    @Autowired
    public AuthorityServiceImpl(AuthorityRepository authorityRepository, UserAuthorityRepository userAuthorityRepository) {
        this.authorityRepository = requireNonNull(authorityRepository);
        this.userAuthorityRepository = requireNonNull(userAuthorityRepository);
    }

    @Override
    public Authority createAuthority(Authority authority) {
        return authorityRepository.save(authority);
    }

    @Override
    public Set<UserAuthority> assignAuthoritiesToUser(User user, Set<Authority> authorities) {
        return userAuthorityRepository.saveAll(
                authorities.stream().map(auth -> {
                    UserAuthority ua = new UserAuthority();
                    ua.setAuthority(auth);
                    ua.setUser(user);
                    ua.setType(UserAuthorityType.ADMIN_ASSIGNED_AUTHORITY);
                    ua.setCreationDate(new Date());
                    ua.setExpirationDate(null);
                    return ua;
                }).collect(Collectors.toSet()))
                .stream().collect(Collectors.toSet());
    }

    @Override
    public Authority findById(Long authorityId) {
        Optional<Authority> authority = authorityRepository.findById(authorityId);
        return authority.isEmpty() ? null : authority.get();
    }
}
