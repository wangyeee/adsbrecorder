package adsbrecorder.common.utils;

import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;

public interface RandomUtils {

    SecureRandom getSecureRandom();

    default String nextSalt(int length) {
        if (length > 0) {
            SecureRandom rand = getSecureRandom();
            byte[] bytes = new byte[length];
            rand.nextBytes(bytes);
            return Hex.encodeHexString(bytes);
        } else if (length == 0) {
            return new String();
        }
        throw new IllegalArgumentException(String.format("Invalid salt length: %d", length));
    }
}
