package kalpas.simple.DO;

import com.google.common.base.Joiner;

public class User {

    public String      uid;
    public String      first_name;
    public String      last_name;
    public String      nickname;
    // 2 - male, 1 - female
    public int         sex;
    public String      bdate;
    public String      city;
    public String      country;
    public String      photo_50;
    public String      photo_100;
    public String      photo_200_orig;
    public String      photo_200;
    public String      photo_max;
    public String      photo_max_orig;
    public String      online;
    public String[]    lists;
    public String      screen_name;
    public String      has_mobile;
    public String      rate;
    public Contacts    contacts;
    public String      education;
    public String      universities;
    public String      schools;
    public String      can_post;
    public String      can_see_all_posts;
    public String      can_write_public_message;
    public String      activity;
    public LastSeen    last_seen;
    // 1 - не женат/не замужем
    // 2 - есть друг/есть подруга
    // 3 - помолвлен/помолвлена
    // 4 - женат/замужем
    // 5 - всё сложно
    // 6 - в активном поиске
    // 7 - влюблён/влюблена
    public int         relation;
    public Counters    counters;
    public String[]    exports;
    public String      wall_comments;
    public Relatives[] relatives;
    public String      interests;
    public String      movies;
    public String      tv;
    public String      books;
    public String      games;
    public String      about;
    public String[]    connections;
    public String      timezone;
    public String      photo;
    public String      photo_medium;
    public String      photo_big;
    public String      domain;

    public User() {
    }

    public User(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return Joiner.on(" ").skipNulls().join(uid, first_name, last_name);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uid == null) ? 0 : uid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        User other = (User) obj;
        if (uid == null) {
            if (other.uid != null) {
                return false;
            }
        } else if (!uid.equals(other.uid)) {
            return false;
        }
        return true;
    }

    public class Contacts {
        public String mobile_phone;
        public String home_phone;
    }

    public class Counters {
        public int albums;
        public int videos;
        public int audios;
        public int notes;
        public int friends;
        public int groups;
        public int online_friends;
        public int mutual_friends;
        public int user_videos;
        public int followers;
        public int user_photos;
        public int subscriptions;

    }

    public class Relatives {
        public String uid;
        // grandchild, grandparent, child, sibling, parent.
        public String type;
    }

    public class LastSeen {
        public String time;
    }
}
