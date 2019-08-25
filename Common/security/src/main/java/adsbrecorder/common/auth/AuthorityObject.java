package adsbrecorder.common.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public interface AuthorityObject {

    AbstractAuthenticationToken toAuthenticationToken();
}
