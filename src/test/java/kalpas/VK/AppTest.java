package kalpas.VK;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import kalpas.simple.api.WallPost;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;

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

    public void test2() {
        WallPost post = null;
        try {
            Gson g = new Gson();
            post = g.fromJson(new InputStreamReader(new FileInputStream(new File("1.json"))), WallPost.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.err.println(post);

    }
}
