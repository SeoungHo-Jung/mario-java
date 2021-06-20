import javax.swing.*;
import java.awt.*;

public class Application {

    public static final int CANVAS_WIDTH = 256;
    public static final int CANVAS_HEIGHT = 240;
    public static final Dimension CANVAS_DIMENSIONS = new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT);
    public static final String NAME = "Mario Bros";

    public static void main(String[] args) {
        Game game = new Game();
        game.setMinimumSize(CANVAS_DIMENSIONS);
        game.setMaximumSize(CANVAS_DIMENSIONS);
        game.setPreferredSize(CANVAS_DIMENSIONS);

        JFrame frame = new JFrame(NAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(game, BorderLayout.CENTER);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        game.run();
    }
}
