package kalpas.VKCore.simple.DO;

import com.google.gson.Gson;

public class VKError extends Exception {

    private static final long serialVersionUID = 2387348004331487567L;

    private VKDetailedError error;

    public VKError(String json) {
        super(json);
    }

    public VKError() {
        super("unknown VK error");
    }

    public String getRobustError(){
        parseIfNot();
        return error.error_msg;
    }

    public int getErrorCode(){
        parseIfNot();
        return Integer.valueOf(error.error_code);
    }

    private void parseIfNot() {
        if(error==null){
            Gson gson = new Gson();
            error = gson.fromJson(getMessage(), ErrorResponse.class).error;
        }
    }

    private class ErrorResponse {
        public VKDetailedError error;
    }

    private class VKDetailedError {
        public String              error_code;
        public String              error_msg;
    }

}
