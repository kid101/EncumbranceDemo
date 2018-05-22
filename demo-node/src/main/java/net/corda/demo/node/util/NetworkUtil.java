package net.corda.demo.node.util;

import net.corda.demo.node.constant.ServiceConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Runtime.getRuntime;

public class NetworkUtil {
    private NetworkUtil() { }

    private static final Logger logger = LoggerFactory.getLogger(NetworkUtil.class);

    public static Boolean checkIfConnectionExists() {
        try {
            Process process = getRuntime().exec("ping " + ServiceConstant.ACCESSIBLE_SITE);
            int x = process.waitFor();
            if (x == 0) {
                logger.info(String.format("Connected to Internet Output received %d", x));
                return true;
            } else {
                logger.info(String.format("Not Connected to Internet, Output received %d", x));
                return false;
            }
        }
        catch (Exception e){
            logger.warn(e.getMessage());
            return false;
        }
    }
}
