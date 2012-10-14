package kalpas.VK;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import kalpas.VK.requests.FriendsGet;
import kalpas.VK.requests.FriendsGetFactory;
import kalpas.VK.requests.base.BaseVKRequest;

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

import com.google.common.base.Strings;

public class VK {
    private static final int    MIN_PAUSE         = 340;

    Logger                      logger            = Logger.getLogger(VK.class);

    private static final String appId             = "3164748";
    private static String       auth              = "oauth.vk.com";

    private String              accessToken       = null;
    private String              secret            = null;
    private String              selfUid           = null;
    private boolean             HTTPS             = false;

    private FriendsGetFactory   friendsGetFactory = null;

    private ExecutorService     pool              = Executors
                                                          .newFixedThreadPool(2);
    static {

    }

    public void auth() {
        this.auth(true);
    }

    public void auth(boolean https) {
        HTTPS = https;
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
                        if (!HTTPS) {
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

        String string = buildAuthURI(https);

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
        return accessToken;
    }

    private FriendsGetFactory getFriendRequestFactory() {
        if (friendsGetFactory == null) {
            friendsGetFactory = HTTPS ? new FriendsGetFactory(accessToken)
                    : new FriendsGetFactory(accessToken, secret);
        }
        return friendsGetFactory;
    }

    public List<VKFriend> getFriendsList() {
        return getFriendsList(selfUid);
    }

    public List<VKFriend> getFriendsList(String uid) {

        List<VKFriend> result = new ArrayList<VKFriend>();
        if (!Strings.isNullOrEmpty(accessToken)) {
            FriendsGet request = getFriendRequestFactory()
                    .createRequestWithFields("uid", "first_name", "last_name",
                            "sex");
            result = request.addUid(uid).execute().getFriends();
        }

        return result;
    }

    public Map<String, List<VKFriend>> getFrinedsOfFriends(
            List<VKFriend> friends) {

        ConcurrentMap<String, List<VKFriend>> friendMap = new ConcurrentHashMap<String, List<VKFriend>>();
        List<Future<Runnable>> result = new ArrayList<Future<Runnable>>();

        for (VKFriend friend : friends) {
            pool.submit(new RunnableFrinedsGet(friend.getUid(), friendMap));
            // result = getFriendsList(friend.getUid());
            // sleepIfNeeded(delta);
        }
        try {
            pool.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.error("awaiting terminated ", e);
        }
        return friendMap;
    }

    public void request(String body) {

        String[] parts = body.split(" ");
        final String name_ = parts[0];
        final String body_ = parts[1];

        BaseVKRequest request = new BaseVKRequest() {

            @Override
            protected String getAccessToken() {
                return accessToken;
            }

            @Override
            public String getBody() {
                return body_;
            }

            @Override
            public String getName() {
                return name_;
            }

        };

        request.send();

    }

    private void sleepIfNeeded(long delta) {
        if (delta * 1E-6 < MIN_PAUSE) {
            Double sleepTime = 340 - delta * 1E-6;
            logger.debug("FAST RESPONSE. SLEEP NEEDED - " + sleepTime);
            try {
                Thread.sleep(sleepTime.longValue());
            } catch (InterruptedException e) {
            }
        }
    }

    private class RunnableFrinedsGet implements Runnable {

        private String                        uid;

        ConcurrentMap<String, List<VKFriend>> map = null;

        public RunnableFrinedsGet(String uid,
                ConcurrentMap<String, List<VKFriend>> map) {
            this.map = map;
            this.uid = uid;
        }

        @Override
        public void run() {
            long start, end;
            start = System.nanoTime();
            List<VKFriend> result = getFriendsList(uid);
            end = System.nanoTime();
            logger.debug("getting friend list took " + (end - start) * 1E-6
                    + " ms");
            if (result != null) {
                map.put(uid, result);
            }
            return;
        }
    }

}
