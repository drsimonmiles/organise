package uk.ac.kcl.inf.organise.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;
import uk.ac.kcl.inf.organise.ui.rules.ReactionChoicesPanel;
import uk.ac.kcl.inf.organise.ui.rules.TriggerChoicesPanel;

public class TaskDetailPanel extends JPanel implements ActionListener, OrganiseEventListener {
    private Task _task;
    private final TaskPanel _summary;
    private final NotesPanel _notes;
    private final TriggerChoicesPanel _triggerChoices;
    private final ReactionChoicesPanel _reactionChoices;
                       
    public TaskDetailPanel (Database database, EventBus bus) {
        _task = null;
        _summary = new TaskPanel (database, bus);
        _notes = new NotesPanel (bus);
        _triggerChoices = new TriggerChoicesPanel ();
        _reactionChoices = new ReactionChoicesPanel ();

        setLayout (new MigLayout ("insets 0 0 0 0", "[0:0,grow 5,fill][0:0,grow 5,fill]", "[pref!][0:0,grow 10,fill][0:0,grow 5,fill]"));
        add (_summary, "span 2,wrap");
        add (new JScrollPane (_notes), "span 2,wrap");
        add (_triggerChoices);
        add (_reactionChoices);
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
