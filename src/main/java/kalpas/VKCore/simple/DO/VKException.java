package kalpas.VKCore.simple.DO;

public class VKException extends Exception {
    private static final long serialVersionUID = 6314346942633535418L;

    private Error error;

    public VKException(Error error) {
        this.error = error;
    }

    public Error getError(){
        return error;
    }

}
