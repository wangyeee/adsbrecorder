package adsbrecorder.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public interface HashUtils {

    String HASH_ALGORITHM = "SHA-512";
    String ENCODING = "UTF-8";

    default byte[] sha512(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(bytes);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    default String hash(String text) {
        return hash(text, null);
    }

    default String hash(String text, String salt) {
        StringBuilder sb = new StringBuilder(text);
        if (salt != null) {
            sb.append(salt);
        }
        try {
            byte[] str = sb.toString().getBytes(ENCODING);
            return Hex.encodeHexString(sha512(str));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
