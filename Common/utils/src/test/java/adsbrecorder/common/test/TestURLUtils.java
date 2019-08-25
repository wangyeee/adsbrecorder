package adsbrecorder.common.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import adsbrecorder.common.utils.URLUtils;

public class TestURLUtils implements URLUtils {

    @Test
    public void testURLlWildcard() {
        Map<String, String> values = Map.of("/", "/",
                "/a/{x}/b", "/a/**/b",
                "/a/b/c", "/a/b/c");
        values.forEach((text, hash) -> {
            assertEquals(hash, urlWildcard(text).toLowerCase());
        });
    }
}
