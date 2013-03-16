package kalpas.VKCore.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugUtils {

    private static Logger logger = LogManager.getLogger(DebugUtils.class.getName());

    public static String traceResponse(InputStream stream) {
        String json = null;
        try {
            json = IOUtils.toString(stream, "UTF-8");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        logger.debug(json);
        return json;
    }

    public static String traceRequest(String request){
        logger.debug(request);
        return request;
    }

}
