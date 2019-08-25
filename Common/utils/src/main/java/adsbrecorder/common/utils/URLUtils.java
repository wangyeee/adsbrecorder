package adsbrecorder.common.utils;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;

public interface URLUtils {

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
}
