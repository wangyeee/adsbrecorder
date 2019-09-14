package adsbrecorder.client.controller;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.client.ClientServiceMappings;
import adsbrecorder.client.entity.RemoteReceiver;
import adsbrecorder.client.service.RemoteReceiverService;
import adsbrecorder.client.service.impl.RemoteReceiverOwnershipChecker;
import adsbrecorder.common.aop.annotation.CheckOwnership;
import adsbrecorder.common.aop.annotation.LoginUser;
import adsbrecorder.common.aop.annotation.RequireLogin;
import adsbrecorder.common.aop.annotation.RequireOwnership;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.service.UserService;

@RestController
public class ClientController implements ClientServiceMappings {

    private RemoteReceiverService receiverService;
    private UserService userService;

    @Autowired
    public ClientController(RemoteReceiverService receiverService, UserService userService) {
        this.receiverService = requireNonNull(receiverService);
        this.userService = requireNonNull(userService);
    }

    @RequireLogin
    @PostMapping(CLIENT_NEW)
    public ResponseEntity<Map<String, Object>> addRemoteReceiver(
            @RequestParam(name = "name", required = true) String name,
            @RequestParam(name = "desc", required = false) String description,
            @LoginUser User owner) {
        RemoteReceiver receiver = receiverService.createRemoteReceiver(name, description, owner);
        if (receiver.toAuthenticationToken().isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("receiver", receiver));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", String.format("Client name %s is already used", name)));
    }

    @RequireOwnership
    @GetMapping(CLIENT_UPDATE_KEY)
    public ResponseEntity<Map<String, String>> getRemoteReceiverKey(@PathVariable("client")
            @CheckOwnership(validator = RemoteReceiverOwnershipChecker.class) Long remoteReceiverID) {
        RemoteReceiver receiver = receiverService.findRemoteReceiver(remoteReceiverID);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "remoteReceiverKey", StringUtils.defaultString(receiver.getRemoteReceiverKey()),
                        "remoteReceiverID", Long.toString(remoteReceiverID)));
    }

    @RequireOwnership
    @PutMapping(CLIENT_UPDATE_KEY)
    public ResponseEntity<RemoteReceiver> regenerateReceiverKey(@PathVariable("client")
            @CheckOwnership(validator = RemoteReceiverOwnershipChecker.class) Long remoteReceiverID) {
        RemoteReceiver receiver = receiverService.findRemoteReceiver(remoteReceiverID);
        receiver.setRemoteReceiverKey(UUID.randomUUID().toString());
        receiver = receiverService.updateRemoteReceiver(receiver);
        receiver.setOwner(null);  // reduce response size
        return ResponseEntity.status(HttpStatus.OK).body(receiver);
    }

    @RequireOwnership
    @GetMapping(CLIENT_UPDATE_DESCRIPTION)
    public ResponseEntity<Map<String, String>> getRemoteReceiverDescription(@PathVariable("client")
            @CheckOwnership(validator = RemoteReceiverOwnershipChecker.class) Long remoteReceiverID) {
        RemoteReceiver receiver = receiverService.findRemoteReceiver(remoteReceiverID);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "description", StringUtils.defaultString(receiver.getDescription()),
                        "remoteReceiverID", Long.toString(remoteReceiverID)));
    }

    @RequireOwnership
    @PutMapping(CLIENT_UPDATE_DESCRIPTION)
    public ResponseEntity<Map<String, String>> updateRemoteReceiver(@PathVariable("client")
            @CheckOwnership(validator = RemoteReceiverOwnershipChecker.class) Long remoteReceiverID,
            @RequestParam(name = "desc", required = true) String description) {
        RemoteReceiver receiver = receiverService.findRemoteReceiver(remoteReceiverID);
        receiver.setDescription(StringEscapeUtils.escapeHtml(description));
        receiver = receiverService.updateRemoteReceiver(receiver);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "description", StringUtils.defaultString(receiver.getDescription()),
                "remoteReceiverID", Long.toString(remoteReceiverID)));
    }

    @PutMapping(CLIENT_UPDATE)
    public RemoteReceiver updateRemoteReceiver(@PathVariable("clientName") String receiverName,
            @RequestBody RemoteReceiver receiver) {
        receiver.setRemoteReceiverKey(receiverName);
        receiverService.updateRemoteReceiver(receiver);
        return receiver;
    }

    @RequireLogin
    @GetMapping(LIST_USER_CLIENTS)
    public List<RemoteReceiver> listUserReceivers(@PathVariable("user") String username,
            @RequestParam(value = "p", required = false, defaultValue = "1") int page,
            @RequestParam(value = "n", required = false, defaultValue = "10") int amount) {
        User owner = userService.findUserByName(username);
        return receiverService.findByOwner(owner)
                .stream().map(rec -> this.trimRemoteReceiver(rec))
                .collect(Collectors.toList());
    }

    @RequireOwnership(allowOverride = true)
    @DeleteMapping(CLIENT_REMOVAL)
    public ResponseEntity<Map<String, String>> removeRemoteReceiver(@PathVariable("client")
            @CheckOwnership(validator = RemoteReceiverOwnershipChecker.class) Long receiverID) {
        RemoteReceiver receiver = receiverService.findRemoteReceiver(receiverID);
        receiverService.removeRemoteReceiver(receiver);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", String.format("RemoteReceiver#%d has been removed", receiverID)));
    }

    @RequireOwnership
    @GetMapping(CLIENT_EXPORT)
    public ResponseEntity<RemoteReceiver> exportRemoteReceiverDetails(@PathVariable("client")
            @CheckOwnership(validator = RemoteReceiverOwnershipChecker.class) Long receiverID) {
        RemoteReceiver receiver = receiverService.findRemoteReceiver(receiverID);
        receiver.setOwner(null);
        return ResponseEntity.status(HttpStatus.OK).body(receiver);
    }

    private RemoteReceiver trimRemoteReceiver(RemoteReceiver receiver) {
        receiver.setOwner(null);
        String key = receiver.getRemoteReceiverKey();
        String suffix = key.substring(key.length() - 4);
        // String suffix = key.substring(key.lastIndexOf('-'));
        char[] padding = new char[key.length() - suffix.length()];
        Arrays.fill(padding, '*');
        receiver.setRemoteReceiverKey(new String(padding).concat(suffix));
        return receiver;
    }
}
