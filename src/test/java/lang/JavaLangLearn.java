package lang;

import org.junit.Assert;
import org.junit.Test;

public class JavaLangLearn {

    private static class TestClone implements Cloneable {
        public TestClone(int j) {
            i = j;
        }

        public int i;
    }

    @Test
    public void testArrayCopy() throws CloneNotSupportedException {
        Integer iarr[] = new Integer[] { 1, 2, 3, 4, 5 };
        Integer[] cloned = iarr.clone();
        iarr[0] = 10;
        // primitive copy are not shadow
        Assert.assertEquals(1, cloned[0].intValue());

        TestClone tc = new TestClone(10);
        TestClone tcArr[] = new TestClone[] { tc };
        TestClone[] copyed = tcArr.clone();
        tc.i = 100;
        // shadow copy
        Assert.assertEquals(100, copyed[0].i);
    }

    @Test
    public void testEqual() {
        Integer i = 11111;
        Integer j = 11111;
        // == && != is broken
        Assert.assertFalse(i == j);

        // System.out.println();
    }
}
