package jcip.puzzle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GuiQueenPuzzleSolver extends JPanel implements KeyListener,
        ActionListener {

    private final QueenPuzzle puzzle;
    private final static int QUEEN_WIDTH_PX = 70;
    private volatile Position currentPos;

    public GuiQueenPuzzleSolver(QueenPuzzle puzzle) {
        this.puzzle = puzzle;
    }

    public List<Move> solve() {
        Position pos = currentPos = puzzle.initialPosition();
        return search(new Node<Position, Move>(pos, null, null));
    }

    private List<Move> search(Node<Position, Move> node) {
        if (puzzle.isGoal(node.pos)) {
            return node.asMoveList();
        }
        for (Move move : puzzle.legalMoves(node.pos)) {
            Position pos = puzzle.move(node.pos, move);
            currentPos = pos;
            repaint();
            waitForContinue();
            Node<Position, Move> child = new Node<Position, Move>(pos, move,
                    node);
            List<Move> result = search(child);
            if (result != null)
                return result;
        }
        return null;
    }

    private void waitForContinue() {
        synchronized (currentPos) {
            try {
                currentPos.wait();
            } catch (InterruptedException consumed) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("queen puzzle");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        QueenPuzzle puzzle = new QueenPuzzle(12);
        GuiQueenPuzzleSolver solver = new GuiQueenPuzzleSolver(puzzle);
        solver.setFocusable(true);
        solver.addKeyListener(solver);
        jFrame.add(solver);
        jFrame.pack();
        jFrame.setVisible(true);
//        new Timer(5500, solver).start();
        List<Move> result = solver.solve();
        System.out.println(result);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyCode());
        actionPerformed(null);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        synchronized (currentPos) {
            currentPos.notify();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(puzzle.getQueenSize() * QUEEN_WIDTH_PX,
                puzzle.getQueenSize() * QUEEN_WIDTH_PX);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final int queenSize = puzzle.getQueenSize();
        final int max = queenSize * QUEEN_WIDTH_PX;
        g.setColor(new Color(0, 0, 60));

        for (int i = 0; i < queenSize; ++i) {
            int step = i * QUEEN_WIDTH_PX;
            g.drawLine(0, step, max, step);
            g.drawLine(step, 0, step, max);
        }

        g.setColor(new Color(255, 0, 0));
        int[] cols = currentPos.cols;
        for (int i = 0; i < cols.length; i++) {
            g.fillRect((cols[i] - 1) * QUEEN_WIDTH_PX, i * QUEEN_WIDTH_PX,
                    QUEEN_WIDTH_PX, QUEEN_WIDTH_PX);
        }
    }
}
