package common;

import java.util.List;

public final class InputCheck {
    private InputCheck() {
    }

    /** For web.abstractServlet.UsernamesServlet - checks if a string input is unique **/
    public static boolean isStringUnique(List<String> usernames, String inputUsername) {
        return usernames.stream().noneMatch((str) -> InputCheck.containsIgnoreCase(str, inputUsername));
    }
    public static boolean containsIgnoreCase(String str, String searchStr) {
        if(str == null || searchStr == null) return false;

        final int length = searchStr.length();
        if (length == 0)
            return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
    }
}
