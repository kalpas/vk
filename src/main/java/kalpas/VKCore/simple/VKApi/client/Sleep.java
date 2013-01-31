package kalpas.VKCore.simple.VKApi.client;

public class Sleep {

    public static Long interval = 340L;

    public static void sleep() {
        if (interval == 0L) {
            return;
        }

        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
