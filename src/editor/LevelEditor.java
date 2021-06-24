package editor;

import javax.swing.*;
import java.awt.*;

public class LevelEditor {
    private JPanel mainPanel;
    private JScrollPane mapScrollPane;
    private JScrollPane toolScrollPane;

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Level Editor");
        frame.setContentPane(new LevelEditor().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(400, 300));
        frame.setPreferredSize(new Dimension(800, 600));
        frame.pack();
        frame.setVisible(true);
    }
}
