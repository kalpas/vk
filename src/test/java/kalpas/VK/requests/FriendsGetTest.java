package kalpas.VK.requests;

import org.junit.Before;
import org.junit.Test;

public class FriendsGetTest {
    
    FriendsGetFactory factory;
    FriendsGet request;

    @Before
    public void before()
    {
        factory = new FriendsGetFactory("someToken");
        request = factory.createRequest();
    }

    @Test
    public void test() {
        System.out.println(request.buildRequest());
    }
    

}
