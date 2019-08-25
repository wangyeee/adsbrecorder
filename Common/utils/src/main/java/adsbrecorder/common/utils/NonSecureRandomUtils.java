package adsbrecorder.common.utils;

import java.util.Random;

import org.apache.commons.codec.binary.Hex;

public interface NonSecureRandomUtils {

    default int defaultFilenameLength() {
        return 10;
    }

    default String nextFilename(int length, String extension) {
        if (length < 0) length = -length;
        if (length == 0) length = 2;
        Random rand = new Random();
        int len0 = length % 2 == 0 ? length / 2 : length / 2 + 1;
        byte[] bytes = new byte[len0];
        rand.nextBytes(bytes);
        StringBuilder sb = new StringBuilder(Hex.encodeHexString(bytes).substring(0, length));
        if (extension != null) {
            if (!extension.startsWith(".")) {
                sb.append(".");
            }
            sb.append(extension);
        }
        return sb.toString();
    }

    default String nextFilename() {
        return nextFilename(defaultFilenameLength(), null);
    }

    default String nextFilename(String extension) {
        return nextFilename(defaultFilenameLength(), extension);
    }

    default String nextFilename(int length) {
        return nextFilename(length, null);
    }
}
