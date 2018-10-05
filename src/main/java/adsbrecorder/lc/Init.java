package adsbrecorder.lc;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import adsbrecorder.jni.AirplaneMonitor;
import adsbrecorder.service.AirlineService;
import adsbrecorder.service.TrackingRecordService;

@Component
public class Init {

    @Value("${adsbrecorder.rtl_device:0}") 
    private int rtlDeviceIndex;

    @Value("${adsbrecorder.rtl_bias_tee:false}") 
    private boolean biasTee;

    private AirlineService airlineService;

    private TrackingRecordService recordService;

    @Autowired
    public Init(AirlineService airlineService, TrackingRecordService recordService) {
        this.airlineService = Objects.requireNonNull(airlineService);
        this.recordService = Objects.requireNonNull(recordService);
    }

    private void loadAirlineData() {
        if (airlineService.checkDefaultAirline()) {
            System.out.println("creating default airline.");
            airlineService.createDefaultAirline();
        }
        if (airlineService.checkKnownAirlines()) {
            System.out.println("loading airline data.");
            airlineService.loadKnownAirlines();
        }
        System.out.println("Airline data loaded.");
    }
    
    private void startMonitor() {
        AirplaneMonitor t = new AirplaneMonitor(rtlDeviceIndex, recordService, biasTee);
        t.start();
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        loadAirlineData();
        startMonitor();
    }
}
