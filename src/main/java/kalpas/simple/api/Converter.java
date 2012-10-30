package kalpas.simple.api;

import kalpas.VK.VKUser;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import com.google.common.base.Strings;

public class Converter {

    private static Logger          logger           = Logger.getLogger(Converter.class);

    static final DateTimeFormatter formatterFull    = DateTimeFormat.forPattern("dd.MM.yyyy").withZoneUTC();
    static final DateTimeFormatter formatterPartial = DateTimeFormat.forPattern("dd.MM").withZoneUTC()
                                                            .withDefaultYear(1972);

    public static VKUser convertFromJSON(JSONObject friend) {
        VKUser vkFriend = new VKUser();
        return convertFromJSON(friend, vkFriend);

    }

    public static VKUser convertFromJSON(JSONObject result, VKUser userInfo) {
        userInfo.setUid(result.optString("uid"));
        userInfo.setFirstName(result.optString("first_name"));
        userInfo.setLastName(result.optString("last_name"));
        userInfo.setNickname(result.optString("nickname"));
        userInfo.setSex(result.optInt("sex"));

        DateTime bdate = null;
        String date = result.optString("bdate");
        if (!Strings.isNullOrEmpty(date) && !"-99.1".equals(date)) {
            try {
                bdate = formatterFull.parseDateTime(date);
            } catch (IllegalArgumentException e) {
                try {
                    bdate = formatterPartial.parseDateTime(date);
                } catch (IllegalArgumentException ex) {
                    logger.error("date not parsed at all", ex);
                }
            } catch (UnsupportedOperationException e) {
                logger.error("smth really bad happened while parsing date", e);
            }
            userInfo.setBdate(bdate);
        }

        userInfo.setCity(result.optString("city"));
        userInfo.setCountry(result.optString("country"));
        return userInfo;
    }

}
