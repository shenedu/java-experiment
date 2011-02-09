package jcip.puzzle;

import java.util.List;

public interface Puzzle<P, M> {
    P initialPosition();

    boolean isGoal(P position);

    List<M> legalMoves(P position);

    P move(P position, M move);
}
