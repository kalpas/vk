package kalpas.VK.requests.base;

import java.io.IOException;
import java.io.InputStream;

import kalpas.VK.HttpClientContainer;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseVKRequest implements VKRequest {

    Logger               logger    = Logger.getLogger(this.getClass());

    // response
    protected JSONObject response  = null;
    protected JSONObject error     = null;
    protected Integer    errorCode = 0;
    protected String     errorMsg  = null;

    HttpClientContainer  container;

    private final String api       = "api.vk.com";

    @Override
    public VKRequest execute() {
        errorCode = 0;
        errorMsg = "";
        error = null;
        response = send();
        return this;
    }

    protected String getAccessToken() {
        throw new UnsupportedOperationException();
    }

    protected String getBody() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getErrorCode() {
        error = response.optJSONObject("error");
        if (error != null) {
            errorCode = error.optInt("error_code");
        }
        return errorCode;
    }

    @Override
    public String getErrorMsg() {
        if (error == null) {
            error = response.optJSONObject("error");
            errorMsg = error == null ? "" : error.optString("error_msg");
        }
        return errorMsg;
    }

    public String getErrorMsg4Log() {
        // String errMsg4Log = "";
        // if (this.error == null) {
        // this.error = this.response.optJSONObject("error");
        // errMsg4Log = this.error == null ? "" : error.optString("error_msg")
        // + (error.optJSONObject("request_params").toString());
        // }
        return response.toString();
    }

    protected HttpClient getHttpClient() {
        return container.getInstance();
    }

    protected Logger getLogger() {
        return logger;
    }

    protected String getSecret() {
        throw new UnsupportedOperationException();
    }

    protected boolean isHttps() {
        return true;
    }

    @Override
    public JSONObject send() {
        String sig = "";
        String request = "/method/" + getName() + "?" + getBody()
                + "&access_token=" + getAccessToken();
        if (!isHttps()) {
            sig = DigestUtils.md5Hex(request + getSecret());
        }
        String fullRequest = (isHttps() ? "https" : "http") + "://" + api
                + request + (!isHttps() ? "&sig=" + sig : "");
        HttpGet get = new HttpGet(fullRequest);
        getLogger().debug(request + " would be executed");
        JSONObject response = null;
        try {
            response = getHttpClient().execute(get,
                    new VKResponseHandler<JSONObject>());
        } catch (ClientProtocolException e) {
            logger.error("ClientProtocolException", e);
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("IO exception", e);
        }
        getLogger().debug(response);
        return response;
    }

    private class VKResponseHandler<T extends JSONObject> implements
            ResponseHandler<JSONObject> {

        @Override
        public JSONObject handleResponse(HttpResponse response)
                throws ClientProtocolException, IOException {

            JSONObject result = null;
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream stream = null;
                try {
                    stream = entity.getContent();
                    result = new JSONObject(IOUtils.toString(stream));
                    stream.close();
                } catch (JSONException e) {
                    logger.error("error parsing JSON ", e);
                } finally {
                    try {
                        stream.close();
                    } catch (Exception e) {
                    }
                }
            }
            return result;
        }

    }
}

/*
 * {"error":{"error_code":15,"error_msg":"Access denied: user deactivated",
 * "request_params"
 * :[{"value":"1","key":"oauth"},{"value":"friends.get","key":"method"
 * },{"value":
 * "6757468","key":"uid"},{"value":"uid,first_name,last_name,sex","key"
 * :"fields"},{"value":
 * "d656d8c785705d3bd5bc3ff4a9d53c12ccdd50cd50c78828576ecbbd9ac30f99b9c8921"
 * ,"key":"access_token"}]}}
 */

