package samj.mario.editor.util;

import java.util.Locale;

public class OsUtil {

    public static boolean isMac() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.US);
        return osName.contains("darwin") || osName.contains("mac");
    }
}
