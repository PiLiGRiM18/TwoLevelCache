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
    private final int CAPACITY = 3;
    List IDList = new ArrayList<>();
    private HashMap myCashe;

    @Before
    public void setUp() throws Exception {
        LOGGER = java.util.logging.Logger.getAnonymousLogger();
        myCashe = new MyCashe(CAPACITY);
        fillIDList(IDList, 3);
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
//        myCashe.put(1, "one");
//        myCashe.put(2, "two");
//        myCashe.put(3, "three");
//        myCashe.put(4, "four");
//        myCashe.put(5, "five");

        fillCashe(myCashe);
        Object o1 = myCashe.get(IDList.get(0));
        Object o2 = myCashe.get(IDList.get(1));
        Object o3 = myCashe.get(IDList.get(2));
        fillCashe(myCashe);

//String s = (String) myCashe.get("asd");

        System.out.printf("");
    }

    private void fillIDList(List list, int count) {
        for (int i = 0; i < count; i++) {
            UUID uuid = UUID.randomUUID();
            list.add(uuid);
        }
    }

    private void fillCashe(HashMap cashe) {
        byte[] bytes = new byte[20];
        new Random().nextBytes(bytes);
        IDList.forEach(o -> cashe.put(o, bytes));
    }
}