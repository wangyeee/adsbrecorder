package adsbrecorder.client;

import adsbrecorder.common.utils.URLUtils;

public interface ClientServiceMappings extends URLUtils {
    String CLIENT_LOGIN = "/api/client/login";
    String CLIENT_NEW = "/api/client/setup/new";
    String CLIENT_UPDATE = "/api/client/setup/{clientName}";
    String CLIENT_UPDATE_DESCRIPTION = "/api/client/setup/{client}/desc";
    String CLIENT_UPDATE_KEY = "/api/client/setup/{client}/key";
    String CLIENT_EXPORT = "/api/client/setup/{client}/export";
    String CLIENT_REMOVAL = "/api/client/setup/{client}/remove";
    String LIST_USER_CLIENTS = "/api/client/list/{user}";
}
