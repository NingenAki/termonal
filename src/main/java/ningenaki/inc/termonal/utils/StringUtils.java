package ningenaki.inc.termonal.utils;

/** Utility methods for working with strings. */
public final class StringUtils {

    private StringUtils() {
        throw new AssertionError("Utility class");
    }

    public static String center(String s, int size, char pad) {
        if (s == null || size <= s.length())
            return s;
        int left = (size - s.length()) / 2;
        int right = size - s.length() - left;
        return String.valueOf(pad).repeat(left)
                + s + String.valueOf(pad).repeat(right);
    }
}