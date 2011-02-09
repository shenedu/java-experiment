package jcip.puzzle;

import java.util.List;

public class QueenPuzzleSolver {

    private final Puzzle<Position, Move> puzzle;

    public QueenPuzzleSolver(QueenPuzzle puzzle) {
        this.puzzle = puzzle;
    }

    public List<Move> solve() {
        Position pos = puzzle.initialPosition();
        return search(new Node<Position, Move>(pos, null, null));
    }

    private List<Move> search(Node<Position, Move> node) {
        if (puzzle.isGoal(node.pos)) {
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

    public static void main(String[] args) {
        QueenPuzzle puzzle = new QueenPuzzle(26);
        QueenPuzzleSolver solver = new QueenPuzzleSolver(puzzle);
        List<Move> result = solver.solve();
        System.out.println(result);
    }
}
