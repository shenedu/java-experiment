package lang;

import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.common.collect.MapMaker;

public class CacheTest {

    private static final Random r = new Random();

    private static class MyKey {
        private int[] value = new int[r.nextInt(1000)];

        public MyKey() {
            for (int i = 0; i < value.length; i++) {
                value[i] = i;
            }
        }
    }

    private static class MyValue {
        private Object[] value = new Object[r.nextInt(10000)];

        public MyValue() {
            for (int i = 0; i < value.length; i++) {
                value[i] = i;
            }
        }
    }

    // use jconsole. No OutOfMemoryError!
    @Test
    public void testCache() {

        ConcurrentMap<Object, Object> map = new MapMaker().softKeys()
                .softValues().makeMap();
        // for (int i = 0; i < 10; i++) {
        // map.put(1, 1);
        //
        // System.out.println(map);
        // }

        for (;;) {
            map.put(new MyKey(), new MyValue());
            // map.get
            try {
                TimeUnit.MILLISECONDS.sleep(r.nextInt(10));
            } catch (InterruptedException ignore) {
            }
        }

    }
}
