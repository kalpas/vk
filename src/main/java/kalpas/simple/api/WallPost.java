package kalpas.simple.api;

import java.util.Map;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({ "online", "post_source", "attachment" })
public class WallPost {

    private String              id;
    private String              toId;
    private String              fromId;
    private DateTime            date;
    private String              text;
    private int                 replyCount;
    private Map<String, Object> comments;
    // private int commentsCount;
    // private int commentsCanPost;
    private Map<String, Object> likes;
    // private int likesCount;
    // private int likesUserLike;
    // private int likesCanLike;
    // private int likesCanPublish;
    private Map<String, Object> reposts;
    // private int repostsCount;
    // private int repostsUserReposted;
    private String              signerId;
    private String              copyOwnerId;
    private String              copyPostId;
    private String              copyTextId;
    private Map<String, Object> attachments;

    public String getId() {
        return id;
    }

    @JsonProperty(value = "id",required=false)
    public WallPost setId(String id) {
        this.id = id;
        return this;
    }

    public String getToId() {
        return toId;
    }

    @JsonProperty(value = "to_id", required = false)
    public WallPost setToId(String toId) {
        this.toId = toId;
        return this;
    }

    public String getFromId() {
        return fromId;
    }

    @JsonProperty(value = "from_id", required = false)
    public WallPost setFromId(String fromId) {
        this.fromId = fromId;
        return this;
    }

    public DateTime getDate() {
        return date;
    }

    @JsonProperty(value = "date", required = false)
    public WallPost setDate(DateTime date) {
        this.date = date;
        return this;
    }

    public String getText() {
        return text;
    }

    @JsonProperty(value = "text", required = false)
    public WallPost setText(String text) {
        this.text = text;
        return this;
    }

    public int getReplyCount() {
        return replyCount;
    }

    @JsonProperty(value = "reply_count", required = false)
    public WallPost setReplyCount(int replyCount) {
        this.replyCount = replyCount;
        return this;
    }

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    @JsonProperty(value = "attachments", required = false)
    public WallPost setAttachments(Map<String, Object> attachments) {
        this.attachments = attachments;
        return this;
    }

    public Map<String, Object> getComments() {
        return comments;
    }

    @JsonProperty(value = "comments", required = false)
    public WallPost setComments(Map<String, Object> comments) {
        this.comments = comments;
        return this;
    }

    public Map<String, Object> getLikes() {
        return likes;
    }

    @JsonProperty(value = "likes", required = false)
    public WallPost setLikes(Map<String, Object> likes) {
        this.likes = likes;
        return this;
    }

    public Map<String, Object> getReposts() {
        return reposts;
    }

    @JsonProperty(value = "reposts", required = false)
    public WallPost setReposts(Map<String, Object> reposts) {
        this.reposts = reposts;
        return this;
    }

    public String getSignerId() {
        return signerId;
    }

    @JsonProperty(value = "signer_id", required = false)
    public WallPost setSignerId(String signerId) {
        this.signerId = signerId;
        return this;
    }

    public String getCopyOwnerId() {
        return copyOwnerId;
    }

    @JsonProperty(value = "copy_owner_id", required = false)
    public WallPost setCopyOwnerId(String copyOwnerId) {
        this.copyOwnerId = copyOwnerId;
        return this;
    }

    public String getCopyPostId() {
        return copyPostId;
    }

    @JsonProperty(value = "copy_post_id", required = false)
    public WallPost setCopyPostId(String copyPostId) {
        this.copyPostId = copyPostId;
        return this;
    }

    public String getCopyTextId() {
        return copyTextId;
    }

    @JsonProperty(value = "copy_text_id", required = false)
    public WallPost setCopyTextId(String copyTextId) {
        this.copyTextId = copyTextId;
        return this;
    }

}
