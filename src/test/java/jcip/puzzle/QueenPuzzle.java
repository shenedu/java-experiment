package jcip.puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Position {
    final int row;
    final int cols[];

    public Position(int row, int[] cols) {
        this.row = row;
        this.cols = cols;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position) {
            Position pos = (Position) obj;
            return pos.row == row && Arrays.equals(cols, pos.cols);
        }
        return false;
    }

    @Override
    public String toString() {

        return "cols: " + Arrays.toString(cols) + "; row: " + row;
    }
}

class Move {
    final int col;

    public Move(int col) {
        this.col = col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Move) {
            Move m = (Move) obj;
            return m.col == col;
        }
        return false;
    }

    @Override
    public String toString() {

        return "" + col;
    }
}

public class QueenPuzzle implements Puzzle<Position, Move> {

    private final int queenSize;

    private int[] allCandidates() {
        int[] candidates = new int[queenSize];
        for (int i = 0; i < queenSize; ++i) {
            candidates[i] = i + 1;
        }
        return candidates;
    }

    public QueenPuzzle(int queenSize) {
        this.queenSize = queenSize;
    }

    @Override
    public Position initialPosition() {

        return new Position(0, new int[] { 0 });
    }

    @Override
    public boolean isGoal(Position position) {
        return position.row == queenSize;
    }

    @Override
    public List<Move> legalMoves(Position position) {
        final int[] cols = position.cols;
        final int[] cadidates = allCandidates();
        int targetRow = position.row + 1;
        if (position.row != 0) {
            for (int i = 0; i < cols.length; i++) {
                final int row = i + 1;
                final int col = cols[i];

                cadidates[col - 1] = -1;

                int left = col - (targetRow - row);
                if (left > 0)
                    cadidates[left - 1] = -1;

                int right = col + (targetRow - row);
                if (right <= queenSize)
                    cadidates[right - 1] = -1;
            }
        }
        List<Move> moves = new ArrayList<Move>();
        for (int i = 0; i < cadidates.length; i++) {
            if (cadidates[i] != -1)
                moves.add(new Move(cadidates[i]));
        }
        return moves;
    }

    @Override
    public Position move(Position position, Move move) {

        if (position.row == 0) {
            return new Position(1, new int[] { move.col });
        }

        int[] cols = new int[position.cols.length + 1];
        System.arraycopy(position.cols, 0, cols, 0, position.cols.length);
        cols[position.cols.length] = move.col;

        return new Position(position.row + 1, cols);
    }

}
