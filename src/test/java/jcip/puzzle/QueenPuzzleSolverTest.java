package jcip.puzzle;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class QueenPuzzleSolverTest {

    private QueenPuzzle puzzle = new QueenPuzzle(4);

    @Test
    public void testLegalMoves() {
        List<Move> moves = puzzle.legalMoves(puzzle.initialPosition());
        Assert.assertEquals(4, moves.size());

        moves = puzzle.legalMoves(new Position(1, new int[] { 1 }));
        Assert.assertEquals(2, moves.size());
        Assert.assertTrue(moves.contains(new Move(3)));
        Assert.assertTrue(moves.contains(new Move(4)));

        moves = puzzle.legalMoves(new Position(1, new int[] { 2 }));
        Assert.assertEquals(1, moves.size());
        Assert.assertTrue(moves.contains(new Move(4)));
    }

    public void testMove() {
        Position position = new Position(0, new int[] { 0 });
        Move move = new Move(1);
        Position p2 = puzzle.move(position, move);
        Assert.assertEquals(p2.row, 1);
        Assert.assertArrayEquals(new int[] { 0, 1 }, p2.cols);

    }

}
