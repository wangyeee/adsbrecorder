package adsbrecorder.common.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import adsbrecorder.common.utils.HashUtils;

public class TestHashUtils implements HashUtils {

    @Test
    public void testHash() {
        Map<String, String> values = Map.of(
                "", "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
                "Java", "5451f194061ed55ba74de19a0c74370e975d43aea32b031c85262b0c71406e5feddb33856a043d45f76c7ef29ad51dba62a650df94a15e8c44bd6aa468a71b54",
                "Hello World", "2c74fd17edafd80e8447b0d46741ee243b7eb74dd2149a0ab1b9246fb30382f27e853d8585719e0e67cbda0daa8f51671064615d645ae27acb15bfb1447f459b");

        values.forEach((text, hash) -> {
            assertEquals(hash, hash(text).toLowerCase());
        });
    }
}
