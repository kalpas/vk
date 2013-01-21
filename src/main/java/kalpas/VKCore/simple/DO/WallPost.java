package kalpas.VKCore.simple.DO;

import java.util.List;

public class WallPost {

    public String       text;
    public String       reply_count;
    public Attachment[] attachments;
    public String       date;       // UNIX date
    public String       online;
    public String       id;
    public PostSource   post_source;
    public String       to_id;
    public String       from_id;
    public Reposts      reposts;
    public Like         likes;
    public Media        media;
    public Comments     comments;
    public String       copy_owner_id;
    public String       copy_post_id;
    public String       copy_text;
    public String       signer_id;

    @Override
    public String toString() {
        return text + "\n" + (attachments == null ? "" : attachments.length + " attachements\n") + likes + "\n"
                + comments;
    }

    public class Attachment {
        public String      type;

        public Photo       photo;
        public Audio       audio;
        public Video       video;
        public Link        link;
        public PostedPhoto posted_photo;
        public Graffiti    graffiti;

        public class Photo {
            public String text;
            public String height;
            public String src_small;
            public String created;
            public String width;
            public String owner_id;
            public String pid;
            public String access_key;
            public String src;
            public String aid;
            public String src_big;
        }

        public class Audio {
            public String duration;
            public String title;
            public String owner_id;
            public String performer;
            public String aid;
        }

        public class Video {
            public String duration;
            public String title;
            public String views;
            public String description;
            public String image_xbig;
            public String owner_id;
            public String image_small;
            public String image;
            public String access_key;
            public String date;
            public String vid;
            public String image_big;
        }

        public class Link {
            public String title;
            public String image_src;
            public String description;
            public String url;
        }

        public class PostedPhoto {
            public String pid;
            public String owner_id;
            public String src;
            public String src_big;
        }

        public class Graffiti {
            public String gid;
            public String owner_id;
            public String src;
            public String src_big;
        }

        public class Poll {
            public String poll_id;
            public String question;
        }

    }

    public class PostSource {
        public String data;
        public String type;

    }

    public class Reposts {
        public String count;
        public String user_reposted;

    }

    public class Media {
        public String thumb_src;
        public String item_id;
        public String owner_id;
        public String type;
    }

    public class Comments {
        public Integer       count;
        public String        can_post;
        public List<Comment> comments;

        @Override
        public String toString() {
            return count + " comments" + (comments == null ? "" : ": " + comments);
        }
    }
}