package kalpas.VK.requests.base;

import org.json.JSONObject;

public interface VKRequest {

    public JSONObject send();

    public VKRequest execute();

    public String getName();
    
    public Integer getErrorCode();

    public String getErrorMsg();

}