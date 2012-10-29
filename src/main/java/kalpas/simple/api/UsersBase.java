package kalpas.simple.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kalpas.simple.VKClient;

import org.apache.log4j.Logger;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;

public abstract class UsersBase {

    static final MapJoiner        joiner         = Joiner.on("&").withKeyValueSeparator("=");
    protected Logger              logger         = Logger.getLogger(Friends.class);
    protected VKClient            client;

    protected List<String>        selectedFields = new ArrayList<String>();
    protected Map<String, String> params         = new HashMap<>();

    private final List<String> allowedFields = Arrays.asList("uid", "first_name", "last_name",
                                                                        "nickname", "sex", "bdate", "city", "country",
                                                                        "timezone", "photo", "photo_medium", "photo_big",
                                                                        "domain", "has_mobile", "rate", "contacts",
                                                                        "education");
    private final List<String> allowedCases = Arrays.asList("nom", "gen", "dat", "acc", "ins", "abl");

    protected String buildRequest(String uid) {
        params.put("uid", uid);
        return joiner.join(params);

    }

    protected UsersBase copy() {
        throw new UnsupportedOperationException();
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
    public UsersBase addFields(String... fields) {
        if (fields.length == 0) {
            throw new IllegalArgumentException("should pass at least one filed name");
        }
        for (String field : fields) {
            if (!allowedFields.contains(field)) {
                throw new IllegalArgumentException(field + " field is Illegal");
            }
        }
    
        selectedFields.addAll(Arrays.asList(fields));
        params.put("fields", Joiner.on(",").skipNulls().join(selectedFields));
        return copy();
    }

    /**
     * 
     * @param nameCase
     *            could be one of "nom", "gen", "dat", "acc", "ins", "abl"
     * @return
     */
    public UsersBase addNameCase(String nameCase) {
        if (!allowedCases.contains(nameCase)) {
            throw new IllegalArgumentException(nameCase + " nameCase is illegal");
        }
    
        params.put("name_case", nameCase);
        return copy();
    }

}
