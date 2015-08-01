package kalpas.VKCore.simple.DO;

public class Comment {
    public String cid;
    public String id;
    public String from_id;
    public String date;
    public String text;
    public Like   likes;

    @Override
    public String toString() {
        return text;
    }
}
