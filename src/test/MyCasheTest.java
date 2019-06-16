package test;

import jdk.nashorn.internal.runtime.logging.Logger;
import main.MyCashe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static java.util.logging.Logger.getAnonymousLogger;

@Logger
public class MyCasheTest {

    private static java.util.logging.Logger LOGGER = getAnonymousLogger();
    private MyCashe<Integer, String> myCashe;

    private final int CAPACITY = 10;

    @Before
    public void setUp() throws Exception {
        cleanTempDirectory();
        myCashe = new MyCashe(CAPACITY, false);
    }

    @After
    public void tearDown() throws Exception {
        cleanTempDirectory();
    }

    @Test
    public void test() {
        fillCashe(myCashe, 10);
        System.out.println(myCashe.toString());
        fillCashe(myCashe, 10);
    }

    private void fillCashe(MyCashe cashe, int count) {
        for (int i = 0; i < count; i++) {
            byte[] bytes = new byte[20];
            new Random().nextBytes(bytes);
            cashe.put(bytes);
//            myCashe.get(0);
        }
    }

    private void cleanTempDirectory() {
        Arrays.stream(new File(System.getProperty("java.io.tmpdir")).listFiles())
                .filter(o -> o.getName().contains("my-cashe")).collect(Collectors.toList())
                .forEach(o -> {
                    File file = o.getAbsoluteFile();
                    LOGGER.log(Level.INFO, "Delete file: " + file);
                    file.delete();
                });
    }
}