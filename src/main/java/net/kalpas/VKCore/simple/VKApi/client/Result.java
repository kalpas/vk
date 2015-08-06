package net.kalpas.VKCore.simple.VKApi.client;

import java.io.InputStream;

public class Result {

    public final InputStream stream;
    public Integer           errCode = null;
    public String            errMsg  = null;

    public Result(InputStream stream) {
        if (stream != null) {
            this.stream = stream;
        } else {
            this.errCode = -1;
            this.stream = null;
        }

    }

    public Result(int errCode) {
        this.stream = null;
        this.errCode = errCode;
    }

}
