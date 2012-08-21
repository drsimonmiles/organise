package uk.ac.kcl.inf.organise.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

public class DeleteIcon implements Icon {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;
    public static final Color COLOUR = Color.red;
    public static final int THICKNESS = 3;
    
    @Override
    public void paintIcon (Component component, Graphics graphics, int x, int y) {
        graphics.setColor (COLOUR);
        for (int line = 0; line < THICKNESS; line += 1) {
            graphics.drawLine (x, y + line, x + WIDTH - 1, y + HEIGHT - 1);
            graphics.drawLine (x + WIDTH - 1, y + line, x, y + HEIGHT - 1);
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
