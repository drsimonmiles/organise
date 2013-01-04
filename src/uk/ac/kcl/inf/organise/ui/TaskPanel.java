package uk.ac.kcl.inf.organise.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.miginfocom.swing.MigLayout;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Priority;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;
import uk.ac.kcl.inf.organise.events.OrganiseEventType;

public class TaskPanel extends JPanel implements ActionListener, DocumentListener, FocusListener, MouseListener, OrganiseEventListener {
    private final Task _task;
    private final EventBus _bus;
    private final Database _database;
    private final JTextField _text;
    private final JTextField _allocated;
    private final JButton _priority, _complete;
    private final JLabel _projectName;
    private final TaskList _list;
    private final boolean _priorityIndependent;
    private boolean _changing;
    
    public TaskPanel (TaskList list, Task task, boolean nameProject, boolean priorityIndependent, Database database, EventBus bus) {
        Font font = new Font ("Lucida Sans Typewriter", Font.PLAIN, 14);
        Insets borderless = new Insets (0, 0, 0, 0);
        
        setBorder (new EmptyBorder (borderless));
        _task = task;
        _bus = bus;
        _database = database;
        _list = list;
        _priorityIndependent = priorityIndependent;
        _changing = false;
        
        _priority = new JButton (new PriorityIcon (task));
        _text = new JTextField (task.getText ());
        _allocated = new JTextField (Integer.toString (task.getAllocated ()));
        _complete = new JButton (new CompleteIcon ());
        
        _text.setFont (font);
        _text.setForeground (Color.black);
        _text.setEditable (true);
        
        if (nameProject) {
            _projectName = new JLabel (task.getProject ());
            setLayout (new MigLayout ("insets 0 0 0 0", "[0:0,grow 4,fill][0:0,grow 10,fill][0:0,grow 80,fill][0:0,grow 4,fill][0:0,grow 4,fill]", "0[pref!]0"));
        } else {
            _projectName = null;
            setLayout (new MigLayout ("insets 0 0 0 0", "[0:0,grow 4,fill][0:0,grow 80,fill][0:0,grow 4,fill][0:0,grow 4,fill]", "0[pref!]0"));
        }
        add (_priority, "gap 0px 0px 0px 0px");
        if (nameProject) {
            add (_projectName, "gap 0px 0px 0px 0px");
        }
        add (_text, "gap 0px 0px 0px 0px");
        add (_allocated, "gap 0px 0px 0px 0px");
        add (_complete, "gap 0px 0px 0px 0px");
        
        _text.addKeyListener (_bus);
        _text.getDocument ().addDocumentListener (this);
        _priority.addActionListener (this);
        _allocated.addKeyListener (_bus);
        _allocated.getDocument ().addDocumentListener (this);
        _complete.addActionListener (this);
        _bus._listeners.add (this);
        _text.getDocument ().addUndoableEditListener (_bus._undo);
        _text.addFocusListener (this);
        _text.addMouseListener (this);
    }

    /**
     * Create a task panel to be displayed separately from a list.
     */
    public TaskPanel (Task task, Database database, EventBus bus) {
        this (null, task, false, true, database, bus);
    }
    
    @Override
    public void actionPerformed (ActionEvent event) {
        if (event.getSource () == _priority) {
            switch (_task.getPriority ()) {
                case normal:
                    _task.setPriority (Priority.urgent);
                    break;
                case urgent:
                    _task.setPriority (Priority.immediate);
                    break;
                case immediate:
                    if (_priorityIndependent) {
                        _task.setPriority (Priority.normal);
                    } else {
                        _task.setPriority (Priority.urgent);
                    }
                    break;
            }
        }
        if (event.getSource () == _complete) {
            _database.completeTask (_task);
        }
    }
    
    public void addTextFieldKeyListener (KeyListener listener) {
        _text.addKeyListener (listener);
    }
    
    @Override
    public void changedUpdate (DocumentEvent occur) {
        documentEvent (occur);
    }
    
    public void documentEvent (DocumentEvent occur) {
        _changing = true;
        _task.setText (_text.getText ());
        try {
            _task.setAllocated (Integer.valueOf (_allocated.getText ()));
        } catch (NumberFormatException notNumber) {
            _task.setAllocated (0);
        }
        _changing = false;
    }
    
    @Override
    public void focusGained (FocusEvent occur) {
        OrganiseEvent previous = _bus.getMostRecent (OrganiseEventType.taskInFocus);
        
        if (previous != null) {
            _bus.event (OrganiseEventType.taskInFocus).task (_task).prior (previous._task).fire ();
        } else {
            _bus.event (OrganiseEventType.taskInFocus).task (_task).fire ();
        }
        if (_list != null) {
            _list.registerCurrentTask (_task);
        }
        _text.setBackground (Color.CYAN);
    }
    
    @Override
    public void focusLost (FocusEvent e) {
        _text.setBackground (Color.WHITE);
    }
    
    public void focusOnText () {
        _text.requestFocusInWindow ();
    }
    
    public Task getTask () {
        return _task;
    }
    
    @Override
    public void insertUpdate (DocumentEvent occur) {
        documentEvent (occur);
    }
    
    @Override
    public void organiseEvent (OrganiseEvent event) {
        switch (event._type) {
            case taskPriorityChanged:
                repaint ();
                break;
            case taskTextChanged:
                if (_task == event._task && !_changing) {
                    _text.setText (_task.getText ());
                    repaint ();
                }
                break;
        }
    }
    
    @Override
    public void removeUpdate (DocumentEvent occur) {
        documentEvent (occur);
    }
    
    @Override
    public void mouseClicked (MouseEvent occur) {
    }
    
    @Override
    public void mousePressed (MouseEvent occur) {
        int reorder = MouseEvent.CTRL_DOWN_MASK | MouseEvent.BUTTON1_DOWN_MASK;
        
        if (_list != null && (occur.getModifiersEx () & reorder) == reorder) {
            _list.insertAfter (_list.getCurrentPanel (), this);
        }
    }
    
    @Override
    public void mouseReleased (MouseEvent e) {
    }
    
    @Override
    public void mouseEntered (MouseEvent e) {
    }
    
    @Override
    public void mouseExited (MouseEvent e) {
    }
}
