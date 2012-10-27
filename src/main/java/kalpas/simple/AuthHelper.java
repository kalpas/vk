package kalpas.simple;

import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class AuthHelper {

    private final Logger  logger = Logger.getLogger(AuthHelper.class);

    private final String  auth   = "oauth.vk.com";
    private final String  appId  = "3164748";

    private final boolean isHttps;

    private String        accessToken;
    private String        secret;
    private String        selfUid;

    @Inject
    public AuthHelper(@Named("isHttps") Boolean https) {
        this.isHttps = https;
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
            @Override
            public void changed(LocationEvent event) {
                if (event.top) {
                    if (event.location
                            .contains("https://oauth.vk.com/blank.html")) {
                        logger.info("response: " + event.location);
                        String[] response = event.location.split("#")[1]
                                .split("&");
                        accessToken = response[0].split("=")[1];
                        selfUid = response[2].split("=")[1];
                        if (!isHttps) {
                            secret = response[3].split("=")[1];
                        }
                        logger.info("access_token = " + accessToken
                                + ", uid = " + selfUid);
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
        builder.setScheme("https")
                .setHost(auth)
                .setPath("/authorize")
                .setParameter("client_id", appId)
                .setParameter("scope",
                        "friends,notify,wall" + (!https ? ",nohttps" : ""))
                .setParameter("redirect_uri", "http://oauth.vk.com/blank.html")
                .addParameter("display", "popup")
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
            logger.fatal("getAccessToken waiting " + Thread.currentThread());
            synchronized (AuthHelper.class) {
                if (accessToken == null) {
                    logger.fatal("getAccessToken entered "
                            + Thread.currentThread());
                    this.auth();
                }
            }
        }
        logger.fatal("getAccessToken returned " + Thread.currentThread());
        return accessToken;
    }

    public String getSecret() {
        if (secret == null) {
            logger.fatal("getSecret waiting " + Thread.currentThread());
            synchronized (AuthHelper.class) {
                if (secret == null) {
                    logger.fatal("getSecret entered " + Thread.currentThread());
                    this.auth();
                }
            }
        }
        logger.fatal("getSecret returned " + Thread.currentThread());
        return secret;
    }

    public String getSelfUid() {
        if (selfUid == null) {
            logger.fatal("getSelfUid waiting " + Thread.currentThread());
            synchronized (AuthHelper.class) {
                if (selfUid == null) {
                    logger.fatal("getSelfUid entered" + Thread.currentThread());
                    this.auth();
                }
            }
        }
        logger.fatal("getSelfUid returned " + Thread.currentThread());
        return selfUid;
    }

}