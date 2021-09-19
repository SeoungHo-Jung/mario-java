package samj.mario.editor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class OsUtil {

    private static final Logger logger = LoggerFactory.getLogger(OsUtil.class);

    public static boolean isMac() {
        String osName = System.getProperty("os.name");
        logger.debug("OS Name: {}", osName);
        return osName.toLowerCase(Locale.US).contains("darwin") || osName.toLowerCase(Locale.US).contains("mac");
    }
}
