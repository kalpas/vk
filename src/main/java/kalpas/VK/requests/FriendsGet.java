package kalpas.VK.requests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kalpas.VK.App;
import kalpas.VK.VKFriend;
import kalpas.VK.requests.base.BaseVKRequest;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.base.Joiner;

public class FriendsGet extends BaseVKRequest {

    private Logger       logger          = Logger.getLogger(App.class);

    private final String requestName     = "friends.get";
    private String       accessToken     = null;
    private String       secret          = null;
    private boolean      https           = true;

    private List<String> allowedFields   = Arrays.asList("uid", "first_name",
                                                 "last_name", "nickname",
                                                 "sex", "bdate", "city",
                                                 "country", "timezone",
                                                 "photo", "photo_medium",
                                                 "photo_big", "domain",
                                                 "has_mobile", "rate",
                                                 "contacts", "education");

    private List<String> allowedCases    = Arrays.asList("nom", "gen", "dat",
                                                 "acc", "ins", "abl");

    private List<String> allowedOrdering = Arrays.asList("name", "hints");

    // request params
    private String       uid             = null;
    private List<String> selectedFields  = new ArrayList<String>();
    private String       nameCase        = null;
    private Integer      count           = null;
    private Integer      offset          = null;
    private String       lid             = null;
    private String       order           = null;

    @SuppressWarnings("unused")
    private FriendsGet() {
    }

    public FriendsGet(String accessTokenn) {
        this.accessToken = accessTokenn;
    }

    public FriendsGet(String accessTokenn, boolean https, String secret) {
        this(accessTokenn);
        this.https = https;
        this.secret = secret;

    }

    // ------------------ METHODS ---------------------------------

    public List<VKFriend> getFriends() {
        if (this.response == null) {
            throw new UnsupportedOperationException();
        }
        List<VKFriend> friends = new ArrayList<VKFriend>();
        if (getErrorCode() == 0) {
            JSONArray friendsArray = response.optJSONArray("response");
            JSONObject friend = null;
            VKFriend vkFriend = null;

            int length = friendsArray.length();
            for (int i = 0; i < length; i++) {
                friend = friendsArray.optJSONObject(i);
                try {
                vkFriend = new VKFriend()
                        .setUid(String.valueOf(friend.optInt("uid")))
                        .setFirstName(friend.optString("first_name"))
                        .setLastName(friend.optString("last_name"))
                        .setSex(friend.optInt("sex"));
                friends.add(vkFriend);
                } catch (NullPointerException e) {
                    logger.fatal("NPE " + friendsArray.toString(), e);
                }
            }
        } else {
            logger.error("error occured " + getErrorCode() + " : "
                    + getErrorMsg4Log());
        }
        return friends.size()==0?null:friends;

    }

    // ------------------ OVERRIDE ---------------------------------

    @Override
    public FriendsGet execute() {
        super.execute();
        return this;
    }

    @Override
    public String getName() {
        return requestName;
    }

    @Override
    public String getBody() {
        return Joiner
                .on("&")
                .skipNulls()
                .join(getUid(), getFileds(), getNameCase(), getCount(),
                        getOffset(), getLid(), getOrder());
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public boolean isHttps() {
        return https;
    }

    @Override
    public String getSecret() {
        return secret;
    }

    // ------------------ BUILDING REQUEST ---------------------------------

    public FriendsGet addUid(String uid) {
        this.uid = uid;
        return this;
    }

    /**
     * 
     * @param field
     *            could be one of "uid", "first_name", "last_name", "nickname",
     *            "sex", "bdate", "city", "country", "timezone", "photo",
     *            "photo_medium", "photo_big", "domain", "has_mobile", "rate",
     *            "contacts", "education"
     * @return
     */
    public FriendsGet addField(String... fields) {
        for (String field : fields) {
            if (allowedFields.contains(field)) {
                selectedFields.add(field);
            } else
                throw new IllegalArgumentException(field + " field is Illegal");
        }
        return this;
    }

    /**
     * 
     * @param nameCase
     *            could be one of "nom", "gen", "dat", "acc", "ins", "abl"
     * @return
     */
    public FriendsGet addNameCase(String nameCase) {
        if (allowedCases.contains(nameCase)) {
            this.nameCase = nameCase;
        } else
            throw new IllegalArgumentException(nameCase
                    + " nameCase is illegal");
        return this;
    }

    public FriendsGet addCount(Integer count) {
        this.count = count;
        return this;
    }

    public FriendsGet addOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public FriendsGet addLid(String lid) {
        this.lid = lid;
        return this;
    }

    /**
     * 
     * @param order
     *            could be "name", "hints"
     * @return
     */
    public FriendsGet addOrder(String order) {
        if (allowedOrdering.contains(order)) {
            this.order = order;
        } else
            throw new IllegalArgumentException(order + "order is illegal");
        return this;
    }

    String getFileds() {
        return !selectedFields.isEmpty() ? "fields="
                + Joiner.on(",").skipNulls().join(selectedFields) : null;
    }

    private String getUid() {
        return uid != null ? "uid=" + uid : null;
    }

    private String getNameCase() {
        return nameCase != null ? "name_case=" + nameCase : null;
    }

    private String getCount() {
        return count != null ? "count=" + count : null;
    }

    private String getOffset() {
        return offset != null ? "offset=" + offset : null;
    }

    private String getLid() {
        return lid != null ? "lid=" + lid : null;
    }

    private String getOrder() {
        return order != null ? "order" + order : null;
    }

}
