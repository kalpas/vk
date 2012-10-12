package kalpas.VK;

import java.util.Collections;
import java.util.List;

import kalpas.VK.requests.FriendsGet;
import kalpas.VK.requests.FriendsGetFactory;
import kalpas.VK.requests.base.BaseVKRequest;

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

    private FriendsGetFactory friendsGetFactory = null;

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
        uriBuilder.append("scope=friends,notify,wall&");
        uriBuilder.append("redirect_uri=http://oauth.vk.com/blank.html&");
        uriBuilder.append("display=page&");
        uriBuilder.append("response_type=token");

        browser.setUrl(uriBuilder.toString());
        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        display.dispose();

    }
    
    public void request(String body) {

        String[] parts = body.split(" ");
        final String name_ = parts[0];
        final String body_ = parts[1];

        BaseVKRequest request = new BaseVKRequest() {

            public String getName() {
                return name_;
            }

            public String getBody() {
                return body_;
            }

            @Override
            protected String getAccessToken() {
                return accessToken;
            }
        };

        request.send();

    }

    public List<String> getFriendsList(String uid) {

        List<String> result = Collections.<String> emptyList();
        if (!Strings.isNullOrEmpty(accessToken)) {
            if (friendsGetFactory == null) {
                friendsGetFactory = new FriendsGetFactory(accessToken);
            }
            FriendsGet request = friendsGetFactory.createRequest();
            request.addUid(uid).send();
        }

        return result;
    }

    public List<String> getFriendsList() {
        return getFriendsList(this.uid);
    }

}
