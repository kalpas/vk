package kalpas.VK;

import org.joda.time.DateTime;

import com.google.common.base.Joiner;

public class VKUser {

    private String   uid;
    private String   firstName;
    private String   lastName;
    private String   nickname;
    private int      sex;
    private DateTime bdate;
    private String   city;
    private String   country;

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

    // FIXME review fields

    // public String getTimezone() {
    // return timezone;
    // }
    //
    // public VKFriend setTimezone(String timezone) {
    // this.timezone = timezone;
    // return this;
    // }
    //
    // public String getPhoto() {
    // return photo;
    // }
    //
    // public VKFriend setPhoto(String photo) {
    // this.photo = photo;
    // return this;
    // }
    //
    // public String getPhotoMedium() {
    // return photoMedium;
    // }
    //
    // public VKFriend setPhotoMedium(String photo_medium) {
    // this.photoMedium = photo_medium;
    // return this;
    // }
    //
    // public String getPhotoBig() {
    // return photoBig;
    // }
    //
    // public VKFriend setPhotoBig(String photo_big) {
    // this.photoBig = photo_big;
    // return this;
    // }
    //
    // public String getDomain() {
    // return domain;
    // }
    //
    // public VKFriend setDomain(String domain) {
    // this.domain = domain;
    // return this;
    // }
    //
    // public String getHasMobile() {
    // return hasMobile;
    // }
    //
    // public VKFriend setHasMobile(String has_mobile) {
    // this.hasMobile = has_mobile;
    // return this;
    // }
    //
    // public String getRate() {
    // return rate;
    // }
    //
    // public VKFriend setRate(String rate) {
    // this.rate = rate;
    // return this;
    // }
    //
    // public String getContacts() {
    // return contacts;
    // }
    //
    // public VKFriend setContacts(String contacts) {
    // this.contacts = contacts;
    // return this;
    // }
    //
    // public String getEducation() {
    // return education;
    // }
    //
    // public VKFriend setEducation(String education) {
    // this.education = education;
    // return this;
    // }

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
