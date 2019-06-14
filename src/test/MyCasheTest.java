package test;

import jdk.nashorn.internal.runtime.logging.Logger;
import main.MyCashe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Logger
public class MyCasheTest {
    private static java.util.logging.Logger LOGGER;

    private final int CAPACITY = 5;
    List<UUID> list = new ArrayList<>();
    private HashMap myCashe;

    @Before
    public void setUp() throws Exception {
        LOGGER = java.util.logging.Logger.getAnonymousLogger();
        myCashe = new MyCashe(CAPACITY);
    }

    @After
    public void tearDown() throws Exception {
        Arrays.stream(new File(System.getProperty("java.io.tmpdir")).listFiles())
                .filter(o -> o.getName().contains("my-cashe")).collect(Collectors.toList())
                .forEach(o -> {
                    File file = o.getAbsoluteFile();
                    LOGGER.log(Level.INFO, "Delete file: " + file);
                    file.delete();
                });
    }

    @Test
    public void test() {
//        myCashe.put("asd", "zxc");
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