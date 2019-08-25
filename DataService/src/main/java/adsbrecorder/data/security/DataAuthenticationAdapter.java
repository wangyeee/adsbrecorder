package adsbrecorder.data.security;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Component;

import adsbrecorder.common.auth.ListOfAuthorities;
import adsbrecorder.common.auth.TokenAuthenticationAdapter;
import adsbrecorder.common.auth.TokenAuthenticationFilter;
import adsbrecorder.data.DataServiceMappings;

@EnableWebSecurity
@Component
public class DataAuthenticationAdapter extends TokenAuthenticationAdapter implements ListOfAuthorities, DataServiceMappings {

    private DataTokenAuthenticationFilter receiverTokenAuthenticationFilter;

    public DataAuthenticationAdapter(DataTokenAuthenticationFilter receiverTokenAuthenticationFilter) {
        this.receiverTokenAuthenticationFilter = requireNonNull(receiverTokenAuthenticationFilter);
    }

    @Override
    protected TokenAuthenticationFilter getTokenAuthenticationFilter() {
        return this.receiverTokenAuthenticationFilter;
    }

    @Override
    protected Collection<String> permitAllURLs() {
        return List.of(
                READ_RECORD,
                READ_GM_KEY,
                GET_TRACK,
                READ_FLIGHT_RECORD,
                READ_FLIGHT_RECORD_EVENT,
                READ_RECENT_FLIGHT);
    }

//    @Override
//    protected Map<String, String> hasAnyAuthorityURLs() {
//        return Map.of(READ_RECORD, ADD_TRACKING_RECORD);
//    }
}
