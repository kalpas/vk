package kalpas.VK;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     * 
     * @param testName
     *            name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     */
    public void testApp() {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("param", Arrays.asList("value"));
        params.put("param2", Arrays.asList("value1", "value2"));
        System.out.println(Joiner.on("&").withKeyValueSeparator("=").join(params));
    }

    public void test1() {
        VKUser a, b;
        a = b = new VKUser();
        a.setUid("uid");

        Function<VKUser, String> getUid = new Function<VKUser, String>() {
            @Override
            public String apply(VKUser input) {
                return input.getUid();
            }
        };

        System.out.println(Joiner.on(",").skipNulls().join(Iterables.transform(Arrays.asList(a, b), getUid)));
    }
}
