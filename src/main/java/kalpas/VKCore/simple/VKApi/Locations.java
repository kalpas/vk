package kalpas.VKCore.simple.VKApi;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import kalpas.VKCore.simple.DO.City;
import kalpas.VKCore.simple.VKApi.client.VKClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Joiner.MapJoiner;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;

public class Locations {

    // public static final String ALL_LOCATIONS =
    // "AU, AT, AZ, AX, AL, DZ, UM, VI, AS, AI, AO, AD, AQ, AG, AR, AM, AW, AF, BS, BD, BB, BH, BZ, BY, BE, BJ, BM, BG, BO, BA, BW, BR, IO, VG, BN, BF, BI, BT, VU, VA, GB, HU, VE, TL, VN, GA, HT, GY, GM, GH, GP, GT, GN, GW, DE, GI, HN, HK, GD, GL, GR, GE, GU, DK, CD, DJ, DM, DO, EU, EG, ZM, EH, ZW, IL, IN, ID, JO, IQ, IR, IE, IS, ES, IT, YE, KP, CV, KZ, KY, KH, CM, CA, QA, KE, CY, KG, KI, CN, CC, CO, KM, CR, CI, CU, KW, LA, LV, LS, LR, LB, LY, LT, LI, LU, MU, MR, MG, YT, MO, MK, MW, MY, ML, MV, MT, MA, MQ, MH, MX, MZ, MD, MC, MN, MS, MM, NA, NR, NP, NE, NG, AN, NL, NI, NU, NC, NZ, NO, AE, OM, CX, CK, HM, PK, PW, PS, PA, PG, PY, PE, PN, PL, PT, PR, CG, RE, RU, RW, RO, US, SV, WS, SM, ST, SA, SZ, SJ, MP, SC, SN, VC, KN, LC, PM, RS, CS, SG, SY, SK, SI, SB, SO, SD, SR, SL, SU, TJ, TH, TW, TZ, TG, TK, TO, TT, TV, TN, TM, TR, UG, UZ, UA, UY, FO, FM, FJ, PH, FI, FK, FR, GF, PF, TF, HR, CF, TD, ME, CZ, CL, CH, SE, LK, EC, GQ, ER, EE, ET, ZA, KR, GS, JM, JP, BV, NF, SH, TC, WF";

    // public static final String COMMON_LOCATIONS = "RU,UA,BY";
    
    protected Logger          logger = LogManager.getLogger(getClass());
    
    private Map<String, City> cities = new HashMap<String, City>();
    
    @Inject
    private MapJoiner          joiner;
    @Inject
    private Gson                     gson;
    
    private VKClient           client;

    @Inject
    public Locations(VKClient client) {
        this.client = client;
    }
    
    public City[] getCities(String countryCode) {
        InputStream stream = client.send("places.getCities" + "?" + "country=" + countryCode);
        City[] result = null;
        try {
            result = gson.fromJson(new InputStreamReader(stream, "UTF-8"), GetCitiesResponse.class).response;
        } catch (JsonSyntaxException | JsonIOException | UnsupportedEncodingException e) {
            logger.error(e);
        }
        return result;
    }

    public City getCityById(String cid) {
        City city = null;
        if ((city = cities.get(cid)) == null) {
            city = loadCityById(cid);
            cities.put(cid, city);
        }
        return city;
    }

    private City loadCityById(String cid) {
        InputStream stream = client.send("places.getCityById" + "?" + "cids=" + cid);
        City[] response = null;
        try {
            response = gson.fromJson(new InputStreamReader(stream, "UTF-8"), GetCitiesResponse.class).response;
        } catch (JsonSyntaxException | JsonIOException | UnsupportedEncodingException e) {
            logger.error(e);
        }
        return (response == null || response.length == 0) ? null : response[0];
    }

    private class GetCitiesResponse {
        public City[] response;
    }
}
