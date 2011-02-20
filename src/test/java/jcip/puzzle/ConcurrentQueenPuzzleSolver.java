package jcip.puzzle;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.concurrent.GuardedBy;

public class ConcurrentQueenPuzzleSolver {

    public static void main(String[] args) throws InterruptedException {
        for (int i = 8; i < 100; ++i) {
            long start = System.currentTimeMillis();
            QueenPuzzle puzzle = new QueenPuzzle(i);
            ConcurrentQueenPuzzleSolver solver = new ConcurrentQueenPuzzleSolver(
                    puzzle);
            List<Move> result = solver.solve();
            System.out.println("solve problem size of " + i + " takes time "
                    + (System.currentTimeMillis() - start) + " ms; result: "
                    + result);
        }
    }

    final ValueLatch<Node<Position, Move>> solution = new ValueLatch<Node<Position, Move>>();
    private final QueenPuzzle puzzle;

    private final ExecutorService exec = Executors.newFixedThreadPool(50);

    public List<Move> solve() throws InterruptedException {
        try {
            Position pos = puzzle.initialPosition();
            exec.execute(newTask(pos, null, null));
            // block until solution is found
            Node<Position, Move> result = solution.getValue();
            return result.asMoveList();
        } finally {
            exec.shutdown();
        }
    }

    protected Runnable newTask(Position p, Move m, Node<Position, Move> prev) {
        return new SolverTask(p, m, prev);
    }

    public ConcurrentQueenPuzzleSolver(QueenPuzzle puzzle) {
        this.puzzle = puzzle;
        ((ThreadPoolExecutor) exec)
                .setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

    }

    class SolverTask extends Node<Position, Move> implements Runnable {

        public SolverTask(Position pos, Move move, Node<Position, Move> prev) {
            super(pos, move, prev);
        }

        @Override
        public void run() {
            if (solution.isSet())
                return;
            if (puzzle.isGoal(pos)) {
                solution.setValue(this);
            } else {
                for (Move move : puzzle.legalMoves(pos)) {
                    Position newPos = puzzle.move(pos, move);
                    if (puzzle.isGoal(newPos)) {
                        solution.setValue(this);
                        return;
                    } else {
                        for (Move m : puzzle.legalMoves(newPos)) {
                            exec.execute(newTask(puzzle.move(newPos, m), m,
                                    this));
                        }
                    }
                }
            }
        }
    }

    static class ValueLatch<T> {

        @GuardedBy("this")
        private final CountDownLatch done = new CountDownLatch(1);
        private T value;

        public boolean isSet() {
            return done.getCount() == 0;
        }

        public synchronized void setValue(T newValue) {
            if (!isSet()) {
                value = newValue;
                done.countDown();
            }
        }

        public T getValue() throws InterruptedException {
            done.await();
            synchronized (this) {
                return value;
            }
        }
    }

}
