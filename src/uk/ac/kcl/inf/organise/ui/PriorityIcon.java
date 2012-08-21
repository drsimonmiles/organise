package uk.ac.kcl.inf.organise.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import uk.ac.kcl.inf.organise.data.Task;

public class PriorityIcon implements Icon {
    public static final int RADIUS = 5;
    private final Task _task;
    
    public PriorityIcon (Task task) {
        _task = task;
    }

    @Override
    public void paintIcon (Component component, Graphics graphics, int x, int y) {
        switch (_task.getPriority ()) {
            case normal:
                graphics.setColor (Color.green);
                break;
            case urgent:
                graphics.setColor (Color.orange);
                break;
            case immediate:
                graphics.setColor (Color.red);
                break;
        }
        graphics.fillOval (x, y, RADIUS * 2, RADIUS * 2);
    }

    @Override
    public int getIconWidth () {
        return RADIUS * 2;
    }

    @Override
    public int getIconHeight () {
        return RADIUS * 2;
    }
}
