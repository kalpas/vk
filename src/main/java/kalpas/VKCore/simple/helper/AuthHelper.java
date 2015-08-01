package kalpas.VKCore.simple.helper;

import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class AuthHelper {

    private static final String PERMISSIONS = "friends,wall,groups,messages";

    private final Logger  logger = LogManager.getLogger(AuthHelper.class);

    private final String  auth   = "oauth.vk.com";
    private final String  appId  = "3164748";

    private final boolean isHttps;

    private String        accessToken;
    private String        secret;
    private String        selfUid;

    @Inject
    public AuthHelper(@Named("isHttps") Boolean https) {
        isHttps = https;
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
            System.out.println("Could not instantiate Browser: " + e.getMessage());
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
            @Override
            public void changed(LocationEvent event) {
                if (event.top) {
                    if (event.location.contains("https://oauth.vk.com/blank.html")) {
                        logger.info("response: " + event.location);
                        String[] response = event.location.split("#")[1].split("&");
                        accessToken = response[0].split("=")[1];
                        selfUid = response[2].split("=")[1];
                        if (!isHttps) {
                            secret = response[3].split("=")[1];
                        }
                        logger.info("access_token = " + accessToken + ", id = " + selfUid);

                        event.doit = false;
                        browser.stop();
                        browser.dispose();
                        display.dispose();
                    }
                }
            }

            @Override
            public void changing(LocationEvent event) {
            }
        });

        shell.open();

        String string = buildAuthURI(isHttps);

        browser.setUrl(string);
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();

    }

    private String buildAuthURI(boolean https) {

        URIBuilder builder = new URIBuilder();
        builder.setScheme("https").setHost(auth).setPath("/authorize").setParameter("client_id", appId)
                .setParameter("scope", PERMISSIONS + (!https ? ",nohttps" : ""))
                .setParameter("redirect_uri", "http://oauth.vk.com/blank.html").addParameter("display", "popup")
                .addParameter("response_type", "token");
        String result = null;
        try {
            result = builder.build().toString();
        } catch (URISyntaxException e) {
            logger.fatal("invalid URL", e);
        }
        return result;
    }

    public String getAccessToken() {
        if (accessToken == null) {
            synchronized (this) {
                if (accessToken == null) {
                    this.auth();
                }
            }
        }
        return accessToken;
    }

    public String getSecret() {
        if (secret == null) {
            synchronized (this) {
                if (secret == null) {
                    this.auth();
                }
            }
        }
        return secret;
    }

    public String getSelfUid() {
        if (selfUid == null) {
            synchronized (this) {
                if (selfUid == null) {
                    this.auth();
                }
            }
        }
        return selfUid;
    }

}
