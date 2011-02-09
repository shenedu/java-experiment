package jcip;

public class Fabo implements Computable<Integer, Integer> {

    private Memorizer<Integer, Integer> m = new Memorizer<Integer, Integer>(
            this);

    @Override
    public Integer compute(Integer arg) throws InterruptedException {
        if (arg < 3)
            return 1;
        else {
            return m.compute(arg - 1) + m.compute(arg - 2);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Fabo f = new Fabo();
        System.out.println(f.compute(5));

    }

}
