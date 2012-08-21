package uk.ac.kcl.inf.organise.ui;

import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.events.EventBus;

public abstract class TaskList extends JPanel implements KeyListener {
    private final Map<Task, TaskPanel> _panels;
    private final Database _database;
    private final EventBus _bus;
    private final boolean _editable;
    private final boolean _priorityIndependent;
    private final boolean _nameProject;
    private Task _current;
    private KeyEvent _lastTyped;

    public TaskList (boolean editable, boolean priorityIndependent, boolean nameProject, Database database, EventBus bus) {
        _panels = new HashMap<> ();
        _editable = editable;
        _priorityIndependent = priorityIndependent;
        _nameProject = nameProject;
        _database = database;
        _bus = bus;
        _current = null;
        _lastTyped = null;

        setLayout (new MigLayout ("insets 0 0 0 0", "0[grow,fill]0", ""));
    }

    private void addPanel (TaskPanel panel, int position, boolean newPanel) {
        add (panel, "wrap 0px, gap 0px 0px 0px 0px", position);
        if (newPanel) {
            panel.addTextFieldKeyListener (this);
        }
    }

    protected abstract void addTask (Task newTask, Task followTask);

    protected abstract Task createTask (String text);

    public void focusOnTask (Task task) {
        _panels.get (task).focusOnText ();
    }

    public TaskPanel getCurrentPanel () {
        return _panels.get (_current);
    }

    private int getCurrentPosition () {
        return getPosition (_current);
    }

    private TaskPanel getPanel (Task task) {
        TaskPanel panel = _panels.get (task);

        if (panel == null) {
            panel = new TaskPanel (this, task, _nameProject, _priorityIndependent, _database, _bus);
            _panels.put (task, panel);
        }

        return panel;
    }

    public int getPosition (Task task) {
        return getTasks ().indexOf (task);
    }

    public TaskPanel getPanelAt (Point point) {
        return (TaskPanel) getComponentAt (point);
    }

    private int getPanelPosition (Task task) {
        Component component;

        for (int index = 0; index < getComponentCount (); index += 1) {
            component = getComponent (index);
            if (component instanceof TaskPanel && ((TaskPanel) component).getTask ().equals (task)) {
                return index;
            }
        }

        return -1;
    }

    protected abstract List<Task> getTasks ();

    public void insertAfter (TaskPanel insertedPanel, TaskPanel aroundPanel) {
        Task inserted = insertedPanel.getTask ();
        Task around = aroundPanel.getTask ();

        _database.moveTaskAfter (inserted, around);
        focusOnTask (inserted);
    }

    @Override
    public void keyPressed (KeyEvent occur) {
        int position = getCurrentPosition ();

        if (occur.getKeyCode () == KeyEvent.VK_UP && position > 0) {
            focusOnTask (getTasks ().get (position - 1));
        }
        if (occur.getKeyCode () == KeyEvent.VK_DOWN && position < (getTasks ().size () - 1)) {
            focusOnTask (getTasks ().get (position + 1));
        }
        if (occur.getKeyCode () == KeyEvent.VK_BACK_SPACE) {
            if (_current.getText ().equals ("")) {
                _database.deleteTask (_current);
                focusOnTask (getTasks ().get (position - 1));
            }
        }
        if (_editable && occur.isControlDown () && occur.getKeyCode () == KeyEvent.VK_P) {
            pasteTasks ();
        }
    }

    @Override
    public void keyReleased (KeyEvent occur) {
    }

    @Override
    public void keyTyped (KeyEvent occur) {
        Task task;

        if (occur == _lastTyped) {
            return;
        }
        _lastTyped = occur;
        if (_editable) {
            if (occur.getKeyChar () == '\n') {
                task = createTask ("");
                addTask (task, getCurrentPanel ().getTask ());
                focusOnTask (task);
            }
        }
    }

    private void pasteTasks () {
        Clipboard clipboard = Toolkit.getDefaultToolkit ().getSystemClipboard ();
        Transferable contents = clipboard.getContents (this);
        Task previous = getCurrentPanel ().getTask ();
        Task next;
        String block;
        String[] lines;

        if (contents != null && contents.isDataFlavorSupported (DataFlavor.stringFlavor)) {
            try {
                block = (String) contents.getTransferData (DataFlavor.stringFlavor);
                lines = block.split ("\n");
                for (int index = 0; index < lines.length; index += 1) {
                    next = createTask (lines[index]);
                    addTask (next, previous);
                    previous = next;
                }
            } catch (UnsupportedFlavorException | IOException ex) {
                ex.printStackTrace ();
            }
        }
    }

    public void refresh () {
        while (getComponentCount () > 0) {
            remove (0);
        }
        tasksAdded (getTasks ());
    }
    
    public void registerCurrentTask (Task current) {
        _current = current;
    }

    public void taskAdded (Task task) {
        int position = getPosition (task);

        addPanel (getPanel (task), position, true);
        validate ();
        repaint ();
    }

    public void tasksAdded (List<Task> tasks) {
        for (Task task : tasks) {
            taskAdded (task);
        }
    }

    public void taskRemoved (Task task) {
        remove (getPanel (task));
        _panels.remove (task);
        validate ();
        repaint ();
    }

    public void taskMoved (Task task) {
        TaskPanel panel = getPanel (task);
        int to = getPosition (task);

        remove (panel);
        addPanel (panel, to, false);
        validate ();
        repaint ();
    }
}
