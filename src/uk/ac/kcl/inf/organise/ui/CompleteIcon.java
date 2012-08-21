package uk.ac.kcl.inf.organise.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

public class CompleteIcon implements Icon {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;
    public static final Color COLOUR = Color.green;
    public static final int THICKNESS = 3;
    
    @Override
    public void paintIcon (Component component, Graphics graphics, int x, int y) {
        graphics.setColor (COLOUR);
        for (int line = 0; line < THICKNESS; line += 1) {
            graphics.drawLine (x, y + HEIGHT / 2, x + WIDTH / 2, y + HEIGHT - 1 - line);
            graphics.drawLine (x + WIDTH / 2, y + HEIGHT - 1 - line, x + WIDTH - 1, y);
        }
    }

    @Override
    public int getIconWidth () {
        return WIDTH;
    }

    @Override
    public int getIconHeight () {
        return HEIGHT;
    }
    
}
