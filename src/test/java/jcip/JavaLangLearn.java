package jcip;

import org.junit.Assert;
import org.junit.Test;

public class JavaLangLearn {

    private static class TestClone implements Cloneable {
        public TestClone(int j) {
            i = j;
        }

        // @Override
        // protected TestClone clone() throws CloneNotSupportedException {
        // return (TestClone) super.clone();
        // }

        public int i;
    }

    @Test
    public void testArrayCopy() throws CloneNotSupportedException {
        Integer iarr[] = new Integer[] { 1, 2, 3, 4, 5 };
        Integer[] cloned = iarr.clone();
        iarr[0] = 10;
        Assert.assertEquals(1, cloned[0].intValue());

        TestClone tc = new TestClone(10);
        // TestClone c2 = tc.clone();   
        // Assert.assertEquals(10, c2.i);

        TestClone tcArr[] = new TestClone[] { tc };
        TestClone[] c3 = tcArr.clone();
        tc.i = 100;
        // shadow copy
        Assert.assertEquals(100, c3[0].i);
    }
}
