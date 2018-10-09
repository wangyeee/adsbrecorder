package adsbrecorder.service;

import adsbrecorder.entity.MilitaryCallsign;

public interface MilitaryCallsignService {

    String PDF_URL = "https://www.live-military-mode-s.eu/pdf/Military%20Callsigns.pdf";

    MilitaryCallsign getRecordByCallsign(String callsign);
    void loadMilitaryCallsignData();
}
