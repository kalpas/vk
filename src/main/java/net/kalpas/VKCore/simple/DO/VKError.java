package net.kalpas.VKCore.simple.DO;

import com.google.gson.Gson;

public class VKError extends Exception {

    private static final long serialVersionUID = -8864228161408956309L;

    private class ErrorResponse {
        public VKDetailedError error;
    }

    private class VKDetailedError {
        public String error_code;
        public String error_msg;
    }

    public static VKError fromJSON(String json) {
        VKError error = new VKError("Generic VK Error");
        error.json = json;
        error.error = new Gson().fromJson(json, ErrorResponse.class).error;
        return error;
    }

    private VKDetailedError   error;

    public String             json;

    public String             id;

    public VKError() {
        super("unknown VK error");
    }

    public VKError(String errMsg) {
        super(errMsg == null ? "unknown VK error" : errMsg);
    }

    public Integer getErrorCode() {
        return error != null ? Integer.valueOf(error.error_code) : -1;
    }

    public String getRobustError() {
        return error != null ? error.error_msg : "no message";
    }

    @Override
    public String toString() {
        return id == null ? "" : id + "# " + getErrorCode() + " " + getRobustError();

    }

}
