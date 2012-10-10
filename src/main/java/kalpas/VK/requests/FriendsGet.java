package kalpas.VK.requests;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import kalpas.VK.requests.base.BaseVKRequest;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.common.base.Joiner;

public class FriendsGet extends BaseVKRequest {

    private final String requestName = "friends.get";
    private String accessToken = null;

    private List<String> allowedFields = Arrays.asList("uid", "first_name",
            "last_name", "nickname", "sex", "bdate", "city", "country",
            "timezone", "photo", "photo_medium", "photo_big", "domain",
            "has_mobile", "rate", "contacts", "education");

    private List<String> allowedCases = Arrays.asList("nom", "gen", "dat",
            "acc", "ins", "abl");

    private List<String> allowedOrdering = Arrays.asList("name", "hints");

    // request params
    private String uid = null;
    private List<String> selectedFields = Collections.<String> emptyList();
    private String nameCase = null;
    private Integer count = null;
    private Integer offset = null;
    private String lid = null;
    private String order = null;

    private HttpClient client = new DefaultHttpClient();

    private FriendsGet() {
    }

    public FriendsGet(String accessTokenn) {
        this.accessToken = accessTokenn;
    }

    public void send() {

    }

    public String getName() {
        return requestName;
    }

    String buildRequest() {
        return Joiner
                .on("&")
                .skipNulls()
                .join(super.buildBaseRequest(), getUid(), getFileds(),
                        getNameCase(), getCount(), getOffset(), getLid(),
                        getOrder(), "accessToken=" + accessToken);
    }

    public FriendsGet addUid(String uid) {
        this.uid = uid;
        return this;
    }

    public FriendsGet addField(String field) {
        if (allowedFields.contains(field)) {
            selectedFields.add(field);
        } else
            throw new IllegalArgumentException(field + " field is Illegal");
        return this;
    }

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
