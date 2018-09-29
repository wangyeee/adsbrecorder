package adsbrecorder.test.jni;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import adsbrecorder.jni.Dump1090Native;

public class TestNativeLibrary {

    @BeforeAll
    public static void loadNativeLibrary() {
        System.loadLibrary("dump1090");
    }

    @Test
    public void testDeviceProbe() throws IOException {
        List<Integer> devs = Dump1090Native.listAllReceivers();
        Assertions.assertTrue(devs.size() > 0, "No RTL device found, tests marked as failed.");
        System.out.println(devs.size() + " RTL devices found.");
        for (Integer idx : devs) {
            System.out.println("Device index: " + idx);
        }
    }

    @Test
    public void testReceive() throws IOException {
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
    }
}
