package adsbrecorder.lc;

import static java.util.Objects.requireNonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import adsbrecorder.jni.AirplaneMonitor;
import adsbrecorder.service.AirlineService;
import adsbrecorder.service.MilitaryCallsignService;
import adsbrecorder.service.TrackingRecordService;

@Component
public class Init {

    @Value("${adsbrecorder.rtl_device:0}") 
    private int rtlDeviceIndex;

    @Value("${adsbrecorder.rtl_bias_tee:false}") 
    private boolean biasTee;

    private AirlineService airlineService;

    private TrackingRecordService recordService;

    private MilitaryCallsignService milCallsignService;

    @Autowired
    public Init(AirlineService airlineService, TrackingRecordService recordService, MilitaryCallsignService milCallsignService) {
        this.airlineService = requireNonNull(airlineService);
        this.recordService = requireNonNull(recordService);
        this.milCallsignService = requireNonNull(milCallsignService);
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
        milCallsignService.loadMilitaryCallsignData();
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
