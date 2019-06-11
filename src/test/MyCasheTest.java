package test;

import main.MyCashe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.stream.Collectors;

public class MyCasheTest {

    LinkedHashMap myCashe;

    @Before
    public void setUp() throws Exception {
        myCashe = new MyCashe(5);
    }

    @After
    public void tearDown() throws Exception {
        Arrays.stream(new File(System.getProperty("java.io.tmpdir"))
                .listFiles())
                .filter(o -> o.getName().contains("MyCashe_"))
                .collect(Collectors.toList())
                .forEach(o -> o.getAbsoluteFile().delete());
    }

    @Test
    public void test() {
        for (int i = 0; i < 11; i++) {
            byte[] bytes = new byte[20];
            new Random().nextBytes(bytes);
            myCashe.put(null, bytes);
        }
        System.out.println();
    }
}