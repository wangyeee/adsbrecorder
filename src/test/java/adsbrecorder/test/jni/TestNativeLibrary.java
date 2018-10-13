package adsbrecorder.test.jni;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import adsbrecorder.jni.Dump1090Native;

public class TestNativeLibrary {

    private static boolean nativeLibraryLoaded;

    @BeforeAll
    public static void loadNativeLibrary() {
        try {
            System.loadLibrary("dump1090");
            nativeLibraryLoaded = true;
        } catch (UnsatisfiedLinkError e) {
            nativeLibraryLoaded = false;
        }
    }

    @Test
    public void testDeviceProbe() throws IOException {
        if (nativeLibraryLoaded) {
            List<Integer> devs = Dump1090Native.listAllReceivers();
            Assertions.assertTrue(devs.size() > 0, "No RTL device found, tests marked as failed.");
            System.out.println(devs.size() + " RTL devices found.");
            for (Integer idx : devs) {
                System.out.println("Device index: " + idx);
            }
        } else {
            System.err.println("No native library available, skipping this test.");
        }
    }

    @Test
    @Disabled("Will be enabled after rtl_close block issue resolved.")
    public void testReceive() throws IOException {
        if (nativeLibraryLoaded) {
            final int timeout = 60;
            Assertions.assertTimeout(Duration.ofSeconds(timeout), () -> {
                final Dump1090Native receiver = Dump1090Native.defaultInstance();
                Assertions.assertNotNull(receiver, "Can't acquire RTL device interface.");
                receiver.startMonitor((aircraft) -> {
                    Assertions.assertNotNull(aircraft, "Empty message received.");
                    try {
                        receiver.stopMonitor();
                    } catch (IOException e) {
                        Assertions.fail(e);
                    }
                });
            }, String.format("No packets received within %d seconds.", timeout));
        } else {
            System.err.println("No native library available, skipping this test.");
        }
    }
}
