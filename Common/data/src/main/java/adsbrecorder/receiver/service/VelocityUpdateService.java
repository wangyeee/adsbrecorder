package adsbrecorder.receiver.service;

import java.util.Collection;
import java.util.List;

import adsbrecorder.receiver.entity.VelocityUpdate;

public interface VelocityUpdateService {

    VelocityUpdate addVelocityUpdate(VelocityUpdate update);
    List<VelocityUpdate> batchCreateVelocityUpdates(Collection<VelocityUpdate> updates, String sourceReceiverName, String sourceReceiverKey);
    void interleavingTrackingRecords(int addressICAO, long lastTime);
}
