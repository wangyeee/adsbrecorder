package adsbrecorder.client.service.impl;

import static java.util.Objects.requireNonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import adsbrecorder.client.entity.RemoteReceiver;
import adsbrecorder.client.service.RemoteReceiverService;
import adsbrecorder.common.validator.OwnershipValidator;
import adsbrecorder.security.exception.ObjectOwnershipException;
import adsbrecorder.user.entity.User;

@Component
public class RemoteReceiverOwnershipChecker implements OwnershipValidator {

    private RemoteReceiverService remoteReceiverService;

    @Autowired
    public RemoteReceiverOwnershipChecker(RemoteReceiverService remoteReceiverService) {
        this.remoteReceiverService = requireNonNull(remoteReceiverService);
    }

    private boolean doCheck(User owner, Long id) {
        RemoteReceiver receiver = remoteReceiverService.findRemoteReceiver(id);
        if (receiver.getRemoteReceiverID() > 0) {
            return receiver.getOwner().getUserId().equals(owner.getUserId());
        }
        return false;
    }

    @Override
    public boolean check(Object owner, Object id) throws ObjectOwnershipException {
        if (owner instanceof User) {
            Long lid = null;
            if (id instanceof Long) {
                lid = (Long) id;
            } else {
                try {
                    lid = Long.valueOf(id.toString());
                } catch (Exception e) {
                    throw new ObjectOwnershipException(e);
                }
            }
            if (doCheck((User) owner, lid))
                return true;
            String message = String.format("RemoteReceiverOwnershipChecker::fail(user=%d, receiver=%d)",
                    ((User) owner).getUserId(), lid);
            throw new ObjectOwnershipException(message);
        }
        throw new ObjectOwnershipException();
    }
}
