package jcip.puzzle;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PQueenPuzzleSolver {

    private final Puzzle<Position, Move> puzzle;
    final CountDownLatch latch = new CountDownLatch(1);

    public PQueenPuzzleSolver(QueenPuzzle puzzle) {
        this.puzzle = puzzle;
    }

    public List<Move> solve() throws InterruptedException, ExecutionException {
        final ExecutorService exec = Executors.newFixedThreadPool(Runtime
                .getRuntime().availableProcessors());
        try {
            final Position pos = puzzle.initialPosition();
            final Node<Position, Move> init = new Node<Position, Move>(pos,
                    null, null);
            CompletionService<List<Move>> completionService = new ExecutorCompletionService<List<Move>>(
                    exec);
            for (final Move m : puzzle.legalMoves(pos)) {
                completionService.submit(new Callable<List<Move>>() {
                    @Override
                    public List<Move> call() throws Exception {
                        Position newPos = puzzle.move(pos, m);
                        return search(new Node<Position, Move>(newPos, m, init));
                    }
                });
            }
            while (true) {
                Future<List<Move>> f = completionService.take();
                List<Move> result = f.get();
                if (result != null)
                    return result;
            }
        } finally {
            exec.shutdownNow();
        }
    }

    private List<Move> search(Node<Position, Move> node) {
        if (latch.getCount() == 0)
            return null;

        if (puzzle.isGoal(node.pos)) {
            latch.countDown();
            return node.asMoveList();
        }
        for (Move move : puzzle.legalMoves(node.pos)) {
            Position pos = puzzle.move(node.pos, move);
            Node<Position, Move> child = new Node<Position, Move>(pos, move,
                    node);
            List<Move> result = search(child);
            if (result != null)
                return result;
        }
        return null;
    }

    public static void main(String[] args) throws InterruptedException,
            ExecutionException {
        for (int i = 8; i < 100; ++i) {
            long start = System.currentTimeMillis();
            QueenPuzzle puzzle = new QueenPuzzle(i);
            PQueenPuzzleSolver solver = new PQueenPuzzleSolver(puzzle);
            List<Move> result = solver.solve();
            System.out.println("solve problem size of " + i + " takes time "
                    + (System.currentTimeMillis() - start) + " ms; result: "
                    + result);
        }
    }
}
