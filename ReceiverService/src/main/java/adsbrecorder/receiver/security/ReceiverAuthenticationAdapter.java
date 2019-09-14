package adsbrecorder.receiver.security;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Component;

import adsbrecorder.common.auth.ListOfAuthorities;
import adsbrecorder.common.auth.TokenAuthenticationAdapter;
import adsbrecorder.common.auth.TokenAuthenticationFilter;
import adsbrecorder.receiver.ReceiverServiceMappings;

@EnableWebSecurity
@Component
public class ReceiverAuthenticationAdapter extends TokenAuthenticationAdapter implements ListOfAuthorities, ReceiverServiceMappings {

    private ReceiverTokenAuthenticationFilter receiverTokenAuthenticationFilter;

    public ReceiverAuthenticationAdapter(ReceiverTokenAuthenticationFilter receiverTokenAuthenticationFilter) {
        this.receiverTokenAuthenticationFilter = requireNonNull(receiverTokenAuthenticationFilter);
    }

    @Override
    protected TokenAuthenticationFilter getTokenAuthenticationFilter() {
        return this.receiverTokenAuthenticationFilter;
    }

//    @Override
//    protected Collection<String> permitAllURLs() {
//        return List.of(remove_DEMO_RECORD);
//    }

    @Override
    protected Map<String, String> hasAnyAuthorityURLs() {
        return Map.of(
            ADD_NEW_RECORDS, ADD_TRACKING_RECORD,
            ADD_VELOCITY_UPDATES, ADD_TRACKING_RECORD
        );
    }
}
