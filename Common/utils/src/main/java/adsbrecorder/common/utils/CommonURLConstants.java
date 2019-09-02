package adsbrecorder.common.utils;

import java.util.Arrays;

public interface CommonURLConstants {

    String PAGE_NUMBER_URL_KEY = "p";
    String AMOUNT_PER_PAGE_URL_KEY = "n";

    String[] filteredRequestKeys = initAndSort(
        AMOUNT_PER_PAGE_URL_KEY,
        PAGE_NUMBER_URL_KEY
    );

    static String[] initAndSort(String ... args) {
        Arrays.sort(args);
        return args;
    }
}
