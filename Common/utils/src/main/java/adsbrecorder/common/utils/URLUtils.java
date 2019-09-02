package adsbrecorder.common.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;

public interface URLUtils extends CommonURLConstants {

    /**
     * /api/client/${name}/delete -> /api/client/** /delete
     * @param mapping
     * @return
     */
    default String urlWildcard(String mapping) {
        mapping = Objects.requireNonNull(mapping);
        String path[] = StringUtils.split(mapping, '/');
        StringBuilder sb = new StringBuilder("/");
        for (String p : path) {
            if (StringUtils.contains(p, '{')) {
                sb.append("**");
            } else {
                sb.append(p);
            }
            sb.append("/");
        }
        if (path.length > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Convert Map<String, String[]> from HttpServletRequest.getParameterMap() to Map<String, String> where the value=value[0
     * @param requestMap return value of HttpServletRequest.getParameterMap()
     * @return simplied Map<String, String> object
     */
    default Map<String, String> simplyRequestMap(Map<String, String[]> requestMap) {
        final String empty = new String();
        Map<String, String> simplied = new HashMap<String, String>(requestMap.size() * 2);
        requestMap.forEach((key, valueArray) -> {
            if (Arrays.binarySearch(filteredRequestKeys, key) < 0) {
                if (valueArray == null || valueArray.length == 0) {
                    simplied.put(key, empty);
                } else {
                    simplied.put(key, valueArray[0]);
                }
            }
        });
        return simplied;
    }
}
