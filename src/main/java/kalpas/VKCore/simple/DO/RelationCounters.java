package kalpas.VKCore.simple.DO;

public class RelationCounters {
    public Integer wallPosts = 0;
    public Integer comments  = 0;
    public Integer likes     = 0;

    public Integer getWeight() {
        return likes + 5 * comments + 15 * wallPosts;
    }
}
