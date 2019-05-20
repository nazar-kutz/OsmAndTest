package vn7.studio.com.osmand.util;

public class StringFormater {
    public static String makeFirstLetterCapital(String text) {
        if (!text.equals("")) {
            return Character.toUpperCase(text.charAt(0)) + text.substring(1);
        }
        return text;
    }
}
