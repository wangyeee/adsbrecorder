package adsbrecorder.jni;

import java.io.IOException;

import adsbrecorder.entity.TrackingRecord;
import adsbrecorder.service.TrackingRecordService;

public class AirplaneMonitor extends Thread implements NewAircraftCallback {

    // is local RTL device found
    private boolean localReceiver;

    private Dump1090Native dump1090;
    private TrackingRecordService recordService;

    public AirplaneMonitor(int deviceIndex, TrackingRecordService recordService) {
        this.recordService = recordService;
        dump1090 = Dump1090Native.getInstance(deviceIndex);
        if (dump1090 == null) {
            System.err.println("START: No RTL device found.");
            localReceiver = false;
        } else {
            localReceiver = true;
        }
        recordService.setLocalReceiver(localReceiver);
    }

    @Override
    public void aircraftFound(Aircraft aircraft) {
        recordService.addRecord(new TrackingRecord(aircraft));
    }

    @Override
    public void run() {
        if (localReceiver) {
            try {
                if (dump1090 != null) {
                    dump1090.startMonitor(this);
                }
            } catch (IOException e) {
            }
        } else {
            System.err.println("Skip local RTL devices.");
        }
    }

    public boolean hasLocalReceiver() {
        return localReceiver;
    }
}
