package adsbrecorder.user.service.impl;

import static java.util.Objects.requireNonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adsbrecorder.user.entity.Authority;
import adsbrecorder.user.repo.AuthorityRepository;
import adsbrecorder.user.service.AuthorityService;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private AuthorityRepository authorityRepository;

    @Autowired
    public AuthorityServiceImpl(AuthorityRepository authorityRepository) {
        this.authorityRepository = requireNonNull(authorityRepository);
    }

    @Override
    public Authority createAuthority(Authority authority) {
        return authorityRepository.save(authority);
    }
}
