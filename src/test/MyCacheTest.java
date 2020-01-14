package test;

import main.MyCache;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static java.util.logging.Logger.getAnonymousLogger;

public class MyCacheTest {

    private static java.util.logging.Logger LOG = getAnonymousLogger();
    private final int CAPACITY = 10;
    private MyCache<UUID, String> myCache;

    @Before
    public void setUp() throws Exception {
        cleanTempDirectory("my-cache.");
        myCache = new MyCache(CAPACITY, false);
    }

    @After
    public void tearDown() throws Exception {
        cleanTempDirectory("my-cache");
    }

    @Test
    public void test() {
        fillCache(myCache, CAPACITY);

    }

    private void fillCache(MyCache cache, int count) {
        for (int i = 0; i < count; i++) {
            byte[] bytes = new byte[20];
            new Random().nextBytes(bytes);
            putFileInCache(cache, bytes);
        }
    }

    private void putFileInCache(MyCache cache, Object file) {
        UUID key = UUID.randomUUID();
        cache.put(key, (Serializable) file);
        LOG.log(Level.SEVERE, "Put the file in cache. \nKey: " + key + " \n");
        System.out.println(cache.toString());
    }

    private void cleanTempDirectory(String nameSubstring) {
        Arrays.stream(new File(System.getProperty("java.io.tmpdir")).listFiles())
                .filter(o -> o.getName().contains(nameSubstring))
                .collect(Collectors.toList())
                .forEach(o -> o.getAbsoluteFile().delete());
    }
}