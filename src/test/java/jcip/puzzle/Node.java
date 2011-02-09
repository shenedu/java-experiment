package jcip.puzzle;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

@Immutable
public class Node<P, M> {

    final P pos;
    final M move;
    final Node<P, M> prev;

    public Node(P pos, M move, Node<P, M> prev) {
        this.pos = pos;
        this.move = move;
        this.prev = prev;
    }

    public List<M> asMoveList() {
        List<M> solution = new LinkedList<M>();
        for (Node<P, M> n = this; n.move != null; n = n.prev) {
            solution.add(0, n.move);
        }
        return solution;
    }

    @Override
    public String toString() {

        return "pos: {" + pos + "}; move: {" + move + "}; prev " + prev;
    }
}
