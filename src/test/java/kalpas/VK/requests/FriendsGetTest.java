package kalpas.VK.requests;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;

public class FriendsGetTest {

    FriendsGetFactory factory;
    FriendsGet        request;
    static {
        BasicConfigurator.configure();
    }

    @Before
    public void before() {
        factory = new FriendsGetFactory("someToken");
        request = factory.createRequest();
    }

    @Test
    public void test() {
        request.addUid("uid");
        request.send();
    }

}
