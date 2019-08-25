package adsbrecorder.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import adsbrecorder.client.ClientServiceMappings;

public class TestURLMapping implements ClientServiceMappings {

    @Test
    public void testURLWildcard() {
        String[] urls = {
                "/api/client/login",
                "/api/client/setup/new",
                "/api/client/setup/{clientName}",
                "/api/client/setup/{clientName}/delete"
        };
        String[] urlsW = {
                "/api/client/login",
                "/api/client/setup/new",
                "/api/client/setup/**",
                "/api/client/setup/**/delete"
        };
        assertEquals(urls.length, urlsW.length);
        for (int i = 0; i < urls.length; i++) {
            String p = urlWildcard(urls[i]);
            assertEquals(p, urlsW[i]);
        }
    }
}
