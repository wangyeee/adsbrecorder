package adsbrecorder.common.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import adsbrecorder.common.utils.NonSecureRandomUtils;

public class TestNSRNG implements NonSecureRandomUtils {

    @Test
    public void testFileNameLength() {
        List<Integer> length = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        length.forEach(l -> {
            String name = nextFilename(l);
            assertEquals(l.intValue(), name.length());
        });
    }

    @Test
    public void testFileNameLengthWithExtension() {
        List<Integer> length = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        length.forEach(l -> {
            String name = nextFilename(l, ".pdf");
            assertEquals(l.intValue() + 4, name.length());
        });
    }

    @Test
    public void testFileNameLengthWithExtensionAutoDot() {
        List<Integer> length = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        length.forEach(l -> {
            String name = nextFilename(l, "pdf");
            assertEquals(l.intValue() + 4, name.length());
        });
    }
}
