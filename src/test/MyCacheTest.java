package test;

import jdk.nashorn.internal.runtime.logging.Logger;
import main.MyCache;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static java.util.logging.Logger.getAnonymousLogger;

@Logger
public class MyCacheTest {

    private static java.util.logging.Logger LOG = getAnonymousLogger();
    private final int CAPACITY = 10;
    private MyCache<Integer, String> myCache;

    @Before
    public void setUp() throws Exception {
        cleanTempDirectory();
        myCache = new MyCache(CAPACITY, false);
    }

    @After
    public void tearDown() throws Exception {
        cleanTempDirectory();
    }

    @Test
    public void test() {
        fillCache(myCache, 10);
        System.out.println(myCache.toString());
        fillCache(myCache, 10);
    }

    private void fillCache(MyCache cache, int count) {
        for (int i = 0; i < count; i++) {
            byte[] bytes = new byte[20];
            new Random().nextBytes(bytes);
            LOG.log(Level.SEVERE, "Put file in cache.");
            cache.put(bytes);
//            myCache.get(0);
        }
    }

    private void cleanTempDirectory() {
        Arrays.stream(new File(System.getProperty("java.io.tmpdir")).listFiles())
                .filter(o -> o.getName().contains("my-cache")).collect(Collectors.toList())
                .forEach(o -> {
                    File file = o.getAbsoluteFile();
                    LOG.log(Level.INFO, "Delete file: " + file);
                    file.delete();
                });
    }
}