package kalpas.VK;

import java.util.Collections;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Strings;

public class VK {
    Logger logger = Logger.getLogger(VK.class);

    private static final String appId = "3164748";
    private static String auth = "https://oauth.vk.com/authorize";

    private String accessToken = null;
    private String uid = null;

    public String getAccessToken() {
        return accessToken;
    }

    public void auth() {
        final Display display = new Display();
        Shell shell = new Shell(display);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        shell.setLayout(gridLayout);

        final Browser browser;
        try {
            browser = new Browser(shell, SWT.NONE);
        } catch (SWTError e) {
            System.out.println("Could not instantiate Browser: "
                    + e.getMessage());
            display.dispose();
            return;
        }
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        browser.setLayoutData(data);

        browser.addLocationListener(new LocationListener() {
            public void changed(LocationEvent event) {
                if (event.top) {
                    if (event.location
                            .contains("https://oauth.vk.com/blank.html")) {
                        logger.info("response: " + event.location);
                        String[] response = event.location.split("#")[1]
                                .split("&");
                        accessToken = response[0].split("=")[1];
                        uid = response[2].split("=")[1];
                        logger.info("access_token = " + accessToken
                                + ", uid = " + uid);
                        display.dispose();
                    }
                }
            }

            public void changing(LocationEvent event) {
            }
        });

        shell.open();

        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(auth);
        uriBuilder.append("?");
        uriBuilder.append("client_id=" + appId + "&");
        uriBuilder.append("scope=friends,stats&");
        uriBuilder.append("redirect_uri=http://oauth.vk.com/blank.html&");
        uriBuilder.append("display=popup&");
        uriBuilder.append("response_type=token");

        browser.setUrl(uriBuilder.toString());
        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        display.dispose();

    }

    public List<String> getFriendsList() {

        List<String> result = Collections.<String> emptyList();
        if (!Strings.isNullOrEmpty(accessToken)) {
            HttpClient client = new DefaultHttpClient();
        }

        return result;
    }


}
