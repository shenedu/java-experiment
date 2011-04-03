package lang;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import org.junit.Test;

public class ReflectionPerformance {

    private static final String[] strs;

    static {
        final String alpha = "abcdefghijklmnopqrstuvwxyz";
        final Random r = new Random();
        final int count = 1000 * 10000;
        strs = new String[count];
        for (int i = 0; i < count; i++) {
            final int size = r.nextInt(10);
            StringBuilder sb = new StringBuilder(size);
            for (int j = 0; j < size; j++) {
                sb.append(alpha.charAt(r.nextInt(alpha.length())));
            }
            strs[i] = sb.toString();
        }

    }

    // 2011/3/7 reflection is about 10/1 the speed of normal call.
    // 9.042s<->0.849s

    @Test
    public void testReflectionTime() throws SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        Method length = String.class.getMethod("length", null);
        Method toUpperCase = String.class.getMethod("toUpperCase", null);

        for (int i = 0; i < strs.length; i++) {
            int l = (Integer) length.invoke(strs[i], null);
            String up = (String) toUpperCase.invoke(strs[i], null);
        }
    }

    @Test
    public void testNoReflectionTime() throws SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        for (int i = 0; i < strs.length; i++) {
            int length = strs[i].length();
            String upperCase = strs[i].toUpperCase();
        }
    }

}
