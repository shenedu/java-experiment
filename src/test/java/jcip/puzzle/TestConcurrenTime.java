package jcip.puzzle;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

public class TestConcurrenTime {

    private long f(int n) {
        if (n < 3)
            return 1;
        else
            return f(n - 1) + f(n - 2);
    }

    @Test
    public void testF() {
        for (int i = 0; i < 3; ++i) {
            long l = System.currentTimeMillis();
            // System.out.println(f(44));
            QueenPuzzle puzzle = new QueenPuzzle(29);
            QueenPuzzleSolver solver = new QueenPuzzleSolver(puzzle);
            List<Move> result = solver.solve();
            System.out.println(System.currentTimeMillis() - l);
        }
    }

    private static class Task implements Runnable {

        private AtomicLong time;

        public Task(AtomicLong time) {
            this.time = time;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            QueenPuzzle puzzle = new QueenPuzzle(29);
            QueenPuzzleSolver solver = new QueenPuzzleSolver(puzzle);
            List<Move> result = solver.solve();
            long t = System.currentTimeMillis() - start;
            time.addAndGet(t);
        }

    }

    @Test
    public void testConcurrentTime() throws InterruptedException {
        for (int j = 24; j > 0; j--) {
            ExecutorService exec = Executors.newFixedThreadPool(j);
            final AtomicLong time = new AtomicLong(0);

            long start = System.currentTimeMillis();
            for (int i = 0; i < 60; ++i) {
                exec.submit(new Task(time));
            }
            exec.shutdown();
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            long htime = System.currentTimeMillis() - start;
            System.out.println(j + " thread total: " + time.get() + " htime "
                    + htime);
        }
    }

}
