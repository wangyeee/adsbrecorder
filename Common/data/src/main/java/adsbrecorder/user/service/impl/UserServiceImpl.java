package adsbrecorder.user.service.impl;

import static java.util.Objects.requireNonNull;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import adsbrecorder.common.utils.HashUtils;
import adsbrecorder.common.utils.RandomUtils;
import adsbrecorder.common.utils.URLUtils;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.repo.UserAuthorityRepository;
import adsbrecorder.user.repo.UserRepository;
import adsbrecorder.user.service.UserRoleService;
import adsbrecorder.user.service.UserService;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class UserServiceImpl implements UserService, HashUtils, RandomUtils, URLUtils {

    private UserRepository userRepository;
    private UserAuthorityRepository userAuthorityRepository;
    private UserRoleService userRoleService;
    private SecureRandom secureRandom;

    @Value("${adsbrecorder.userservice.saltlength:16}")
    private int saltLength;

    @Value("${adsbrecorder.userservice.sha_key}")
    private String secretSigningKey;

    private transient byte[] signingKey;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
            UserAuthorityRepository userAuthorityRepository,
            UserRoleService userRoleService) {
        this.userRepository = requireNonNull(userRepository);
        this.userAuthorityRepository = requireNonNull(userAuthorityRepository);
        this.userRoleService = requireNonNull(userRoleService);
        this.secureRandom = new SecureRandom();
    }

    @PostConstruct
    public void decodeKey() {
        signingKey = Decoders.BASE64.decode(requireNonNull(secretSigningKey));
    }

    @Override
    public User login(String username, String password) {
        User loginUser = authenticate(username, password);
        if (loginUser != null)
            return authorize(loginUser);
        return null;
    }

    @Override
    public User loginHash(String username, String passwordHash) {
        Optional<User> user = userRepository.findOneByUsername(username);
        if (user.isPresent()) {
            User loginUser = user.get();
            char[] loginHash = passwordHash.toCharArray();
            char[] existingHash = loginUser.getPassword().toCharArray();
            final int maxLen = loginHash.length > existingHash.length ? loginHash.length : existingHash.length;
            char[] cs0 = new char[maxLen];
            char[] cs1 = new char[maxLen];
            Arrays.fill(cs0, Character.MIN_VALUE);
            Arrays.fill(cs1, Character.MIN_VALUE);
            System.arraycopy(loginHash, 0, cs0, 0, loginHash.length);
            System.arraycopy(existingHash, 0, cs1, 0, existingHash.length);
            boolean eq = true;
            for (int i = 0; i < maxLen; i++) {
                if (cs0[i] != cs1[i]) eq = false;
            }
            Arrays.fill(cs0, Character.MIN_VALUE);
            Arrays.fill(cs1, Character.MIN_VALUE);
            Arrays.fill(loginHash, Character.MIN_VALUE);
            Arrays.fill(existingHash, Character.MIN_VALUE);
            return eq ? authorize(loginUser) : null;
        }
        return null;
    }

    @Override
    public User authenticate(String username, String password) {
        Optional<User> user = userRepository.findOneByUsername(username);
        if (user.isPresent()) {
            User loginUser = user.get();
            String inputHash = hash(password, loginUser.getSalt());
            if (inputHash.equalsIgnoreCase(loginUser.getPassword())) {
                return loginUser;
            }
        }
        return null;
    }

    @Override
    public User authorize(User user) {
        user.setUserRoles(userRoleService.getUserRoles(user));
        user.setDirectAuthorities(userAuthorityRepository.findAllByUser(user)
                .stream().collect(Collectors.toSet()));
        return user;
    }

    @Override
    public boolean isUsernameExist(String username) {
        Optional<User> user = userRepository.findOneByUsername(username);
        return user.isPresent();
    }

    @Override
    public User createNewUser(String username, String password) {
        User user = new User();
        String salt = nextSalt(this.saltLength);
        user.setUsername(username);
        user.setPassword(hash(password, salt));
        user.setSalt(salt);
        user.setCreationDate(new Date());
        return userRoleService.assignDefaultRolesToUser(userRepository.save(user));
    }

    @Override
    public SecureRandom getSecureRandom() {
        return this.secureRandom;
    }

    @Override
    public User findUserByName(String username) {
        Optional<User> user = userRepository.findOneByUsername(username);
        return user.isPresent() ? authorize(user.get()) : User.getUnauthorizedUser();
    }

    @Override
    public User findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.isPresent() ? authorize(user.get()) : User.getUnauthorizedUser();
    }
    
    @Override
    public Key getSecretSigningKey() {
        return Keys.hmacShaKeyFor(signingKey);
    }

    @Override
    public List<User> findUsers(Map<String, String[]> criteria0, int page0, int amount, long[] matchAmount) {
        Map<String, String> criteria = simplyRequestMap(criteria0);
        if (criteria.isEmpty()) {
            if (matchAmount != null && matchAmount.length == 1) {
                long allUserCount = userRepository.count();
                matchAmount[0] = allUserCount;
            }
            return userRepository.findAll(page0, amount);
        }
        // TODO handle query criterias
        return List.of();
    }
}
