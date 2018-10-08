package adsbrecorder.service;

import adsbrecorder.entity.MilitaryCallsign;

public interface MilitaryCallsignService {

    MilitaryCallsign getRecordByCallsign(String callsign);
    void loadMilitaryCallsignData();
}
