package adsbrecorder.common.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

import org.junit.jupiter.api.Test;

import adsbrecorder.common.utils.RandomUtils;

public class TestRNG implements RandomUtils {

    private SecureRandom secureRandom;

    public TestRNG() {
        try {
            // https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#SecureRandom
            this.secureRandom = SecureRandom.getInstance("Windows-PRNG");
        } catch (NoSuchAlgorithmException e) {
            this.secureRandom = new SecureRandom();
        }
    }

    @Test
    public void testNextSalt() {
        List<Integer> ls = List.of(20, 21, 22, 1, 5, 0);
        ls.forEach(length -> {
            String salt = nextSalt(length);
            assertEquals(length * 2, salt.length());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            nextSalt(-1);
            nextSalt(-2);
        });
    }

    @Override
    public SecureRandom getSecureRandom() {
        return this.secureRandom;
    }
}
