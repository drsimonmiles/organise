package uk.ac.kcl.inf.organise.ui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;
import uk.ac.kcl.inf.organise.ui.rules.RuleEditingPanel;

public class TaskDetailPanel extends JPanel implements OrganiseEventListener {
    private Task _task;
    private final TaskPanel _summary;
    private final NotesPanel _notes;
    private final RuleEditingPanel _rules;
                       
    public TaskDetailPanel (Database database, EventBus bus) {
        _task = null;
        _summary = new TaskPanel (database, bus);
        _notes = new NotesPanel (bus);
        _rules = new RuleEditingPanel (database, bus);

        setLayout (new MigLayout ("insets 0 0 0 0", "[0:0,grow,fill]", "[pref!][0:0,grow 5,fill][0:0,grow 15,fill]"));
        add (_summary, "wrap");
        add (new JScrollPane (_notes), "wrap");
        add (_rules);
        bus._listeners.add (this);
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
        _rules.open (task);
        repaint ();
    }
}
