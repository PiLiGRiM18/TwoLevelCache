package test;

import main.MyCashe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class MyCasheTest {

    MyCashe myCashe;

    @Before
    public void setUp() throws Exception {
        myCashe = new MyCashe();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        for (int i =0; i<10; i++) {
            byte[] bytes = new byte[20];
            new Random().nextBytes(bytes);
            myCashe.put(bytes);
        }
    }
}