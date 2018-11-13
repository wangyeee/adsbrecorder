package adsbrecorder.lc;

import static java.util.Objects.requireNonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger logger = LoggerFactory.getLogger(Init.class);

    @Value("${adsbrecorder.disable_local_receiver:false}") 
    private boolean disableLocalReceiver;

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
            logger.info("creating default airline.");
            airlineService.createDefaultAirline();
        }
        if (airlineService.checkKnownAirlines()) {
            logger.info("loading airline data.");
            airlineService.loadKnownAirlines();
        }
        milCallsignService.loadMilitaryCallsignData();
        logger.info("Airline data loaded.");
    }
    
    private void startMonitor() {
        AirplaneMonitor t = new AirplaneMonitor(rtlDeviceIndex, recordService, biasTee);
        t.start();
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        loadAirlineData();
        if (!disableLocalReceiver) {
            logger.info("Local receivers have been disabled.");
            startMonitor();
        }
    }
}
