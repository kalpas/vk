package kalpas.VKCore.simple.VKApi;

import static org.junit.Assert.assertFalse;

import java.util.List;

import kalpas.VK.BaseApiTest;
import kalpas.VKCore.simple.DO.Dialog;
import kalpas.VKCore.simple.DO.VKError;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MessagesTest extends BaseApiTest {

    private Messages messages;

    @Before
    public void setup() {
        messages = getInjector().getInstance(Messages.class);
    }

    @After
    public void tearDown() {
        messages = null;
    }

    @Test
    public void test() throws VKError {
        //do
        List<Dialog> result = messages.getDialogs(10);
        
        //assert
        assertFalse(result.isEmpty());
    }

    @Test
    public void testWithOffset() throws VKError {
        //do
        List<Dialog> result = messages.getDialogs(10, 10);

        // verify
        assertFalse(result.isEmpty());
    }

}
