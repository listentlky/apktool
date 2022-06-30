package config.utils;

public class TextUtils {

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.equals("null") || str.length() == 0;
    }
}
