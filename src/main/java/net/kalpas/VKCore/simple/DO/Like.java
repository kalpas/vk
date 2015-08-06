package net.kalpas.VKCore.simple.DO;

public class Like {
    public String    can_publish;
    public String    can_like;
    public String    user_likes;
    public Integer   count;
    public String[] items;

    @Override
    public String toString() {
        return count + " likes" + (items == null ? "" : ": " + items.toString());
    }
}