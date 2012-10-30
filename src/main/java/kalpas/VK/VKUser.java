package kalpas.VK;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.google.common.base.Joiner;

public class VKUser {

    private String   uid;
    private String   firstName;
    private String   lastName;
    private String   nickname;
    // 2 - male, 1 - female
    private int      sex;
    private DateTime bdate;
    private String   city;
    private String   country;

    private Map<String, String> values = new HashMap<String, String>();

    // private String photo_50;
    // private String photo_100;
    // private String photo_200_orig;
    // private String photo_200;
    // private String photo_max;
    // private String photo_max_orig;
    // private String online;
    // private String lists;
    // private String screen_name;
    // private String has_mobile;
    // private String rate;
    // private String education;
    // private String universities;
    // private String schools;
    // private String can_post;
    // private String can_see_all_posts;
    // private String can_write_private_message;
    // private String activity;
    // private String last_seen;
    //
    // // 1 - не женат/не замужем
    // // 2 - есть друг/есть подруга
    // // 3 - помолвлен/помолвлена
    // // 4 - женат/замужем
    // // 5 - всё сложно
    // // 6 - в активном поиске
    // // 7 - влюблён/влюблена
    // private int relation;
    // private int albums;
    // private int videos;
    // private int audios;
    // private int notes;
    // private int friends;
    // private int groups;
    // private int online_friends;
    // private int mutual_friends;
    // private int user_videos;
    // private int followers;
    // private int user_photos;
    // private int subscriptions;
    // private String wall_comments;
    // private String relatives;
    // private String interests;
    // private String movies;
    // private String tv;
    // private String books;
    // private String games;
    // private String about;
    // private String connections;

    // private String timezone;
    // private String photo;
    // private String photoMedium;
    // private String photoBig;
    // private String domain;
    // private String hasMobile;
    // private String rate;
    // private String contacts;
    // private String education;

    public String getUid() {
        return uid;
    }

    public VKUser setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public VKUser setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public VKUser setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public VKUser setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public int getSex() {
        return sex;
    }

    public VKUser setSex(int sex) {
        this.sex = sex;
        return this;
    }

    public DateTime getBdate() {
        return bdate;
    }

    public VKUser setBdate(DateTime bdate) {
        this.bdate = bdate;
        return this;
    }

    public String getCity() {
        return city;
    }

    public VKUser setCity(String city) {
        this.city = city;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public VKUser setCountry(String country) {
        this.country = country;
        return this;
    }

    @Override
    public String toString() {
        return Joiner.on(" ").skipNulls().join(uid, firstName, lastName);
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
        VKUser other = (VKUser) obj;
        if (uid == null) {
            if (other.uid != null) {
                return false;
            }
        } else if (!uid.equals(other.uid)) {
            return false;
        }
        return true;
    }

}
