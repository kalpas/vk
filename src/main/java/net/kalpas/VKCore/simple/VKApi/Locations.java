package net.kalpas.VKCore.simple.VKApi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner.MapJoiner;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.kalpas.VKCore.simple.DO.City;
import net.kalpas.VKCore.simple.DO.VKError;
import net.kalpas.VKCore.simple.VKApi.client.Result;
import net.kalpas.VKCore.simple.VKApi.client.VKClient;

@Component
public class Locations {

    // public static final String ALL_LOCATIONS =
    // "AU, AT, AZ, AX, AL, DZ, UM, VI, AS, AI, AO, AD, AQ, AG, AR, AM, AW, AF, BS, BD, BB, BH, BZ, BY, BE, BJ, BM, BG, BO, BA, BW, BR, IO, VG, BN, BF, BI, BT, VU, VA, GB, HU, VE, TL, VN, GA, HT, GY, GM, GH, GP, GT, GN, GW, DE, GI, HN, HK, GD, GL, GR, GE, GU, DK, CD, DJ, DM, DO, EU, EG, ZM, EH, ZW, IL, IN, ID, JO, IQ, IR, IE, IS, ES, IT, YE, KP, CV, KZ, KY, KH, CM, CA, QA, KE, CY, KG, KI, CN, CC, CO, KM, CR, CI, CU, KW, LA, LV, LS, LR, LB, LY, LT, LI, LU, MU, MR, MG, YT, MO, MK, MW, MY, ML, MV, MT, MA, MQ, MH, MX, MZ, MD, MC, MN, MS, MM, NA, NR, NP, NE, NG, AN, NL, NI, NU, NC, NZ, NO, AE, OM, CX, CK, HM, PK, PW, PS, PA, PG, PY, PE, PN, PL, PT, PR, CG, RE, RU, RW, RO, US, SV, WS, SM, ST, SA, SZ, SJ, MP, SC, SN, VC, KN, LC, PM, RS, CS, SG, SY, SK, SI, SB, SO, SD, SR, SL, SU, TJ, TH, TW, TZ, TG, TK, TO, TT, TV, TN, TM, TR, UG, UZ, UA, UY, FO, FM, FJ, PH, FI, FK, FR, GF, PF, TF, HR, CF, TD, ME, CZ, CL, CH, SE, LK, EC, GQ, ER, EE, ET, ZA, KR, GS, JM, JP, BV, NF, SH, TC, WF";

    // public static final String COMMON_LOCATIONS = "RU,UA,BY";
    
    protected Logger          logger = LogManager.getLogger(getClass());
    
    private Map<String, City> cities = new HashMap<String, City>();

    @Autowired
    private MapJoiner          joiner;
    @Autowired
    private Gson                     gson;
    
    private VKClient           client;

    @Autowired
    public Locations(VKClient client) {
        this.client = client;
    }
    
    public City[] getCities(String countryCode) throws VKError {
        Result result = client.send("database.getCities" + "?" + "country_id=" + countryCode);
        if (result.errCode != null) {
            VKError error = new VKError(result.errMsg);
            throw error;
        }

        City[] cities = null;
        try {
            String json = IOUtils.toString(result.stream, "UTF-8");
            GetCitiesResponse fromJson = gson.fromJson(json, GetCitiesResponse.class);
            if (fromJson.response == null) {
                VKError error = VKError.fromJSON(json);
                throw error;
            }
            cities = fromJson.response.items;
        } catch (JsonSyntaxException | JsonIOException | UnsupportedEncodingException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
        return cities;
    }

    public City getCityById(String cid) throws VKError {
        City city = null;
        if (cid != null && !"0".equals(cid) && (city = cities.get(cid)) == null) {
            city = loadCityById(cid);
            cities.put(cid, city);
        }
        return city;
    }

    private City loadCityById(String cid) throws VKError {
        Result result = client.send("database.getCitiesById" + "?" + "city_ids=" + cid);
        if (result.errCode != null) {
            VKError error = new VKError(result.errMsg);
            throw error;
        }

        GetCitiesByIdResponse fromJson = null;
        try {
            String json = IOUtils.toString(result.stream, "UTF-8");
            fromJson = gson.fromJson(json, GetCitiesByIdResponse.class);
            if (fromJson.response == null) {
                VKError error = VKError.fromJSON(json);
                throw error;
            } else if (fromJson.response.length == 0) {
                throw new VKError("no such city");
            }
        } catch (JsonSyntaxException | JsonIOException | UnsupportedEncodingException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
        return fromJson.response[0];
    }

    private class GetCitiesResponse {
        public Cities response;

        public class Cities {
            @SuppressWarnings("unused")
            public int    count;
            public City[] items;
        }
    }

    private class GetCitiesByIdResponse {
        public City[] response;
    }
}
