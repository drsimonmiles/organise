package uk.ac.kcl.inf.organise.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;

public class TaskDetailPanel extends JPanel implements ActionListener, OrganiseEventListener {
    private Task _task;
    private final TaskPanel _summary;
    private final NotesPanel _notes;
                       
    public TaskDetailPanel (Database database, EventBus bus) {
        _task = null;
        _summary = new TaskPanel (database, bus);
        _notes = new NotesPanel (bus);

        setLayout (new MigLayout ("insets 0 0 0 0", "[0:0,grow,fill]", "[pref!][0:0,grow,fill]"));
        add (_summary, "wrap");
        add (_notes);
        bus._listeners.add (this);
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        
    }

    @Override
    public void organiseEvent (OrganiseEvent event) {
        switch (event._type) {
            case taskInFocus:
                open (event._task);
                break;
        }
    }
    
    public void open (Task task) {
        _task = task;
        _summary.open (task);
        repaint ();
    }
}
