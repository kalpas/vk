package kalpas.VK;

public class Controller
{
    
    private VK VK = new VK();

    private static Controller instance = new Controller();

    private Controller()
    {
    }

    public static Controller getInstance()
    {
        return instance;
    }
    
    public void start()
    {
        VK.auth();
        return;
    }

}
