package test;

import jdk.nashorn.internal.runtime.logging.Logger;
import main.MyCashe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Logger
public class MyCasheTest {

    private final int CAPACITY = 5;
    private HashMap myCashe;

    @Before
    public void setUp() throws Exception {
        myCashe = new MyCashe(CAPACITY);
    }

    List<UUID> list = new ArrayList<>();

    @After
    public void tearDown() throws Exception {
        Arrays.stream(new File(System.getProperty("java.io.tmpdir")).listFiles())
                .filter(o -> o.getName().contains("MyCashe_")).collect(Collectors.toList())
                .forEach(o -> o.getAbsoluteFile().delete());
    }

    @Test
    public void test() {
        myCashe.put("asd", "zxc");
        addRandomData(7);
        Object o1 = myCashe.get(list.get(2));
        Object o2 = myCashe.get(list.get(0));
        Object o3 = myCashe.get(list.get(6));
        addRandomData(4);
        Object o4 = myCashe.get(list.get(6));
        Object o5 = myCashe.get(list.get(4));

//String s = (String) myCashe.get("asd");

        System.out.printf("");
    }

    private void addRandomData(int count) {
        for (int i = 0; i < count; i++) {
            byte[] bytes = new byte[20];
            new Random().nextBytes(bytes);
            UUID uuid = UUID.randomUUID();
            list.add(uuid);
            myCashe.put(uuid, bytes);
        }
    }
}