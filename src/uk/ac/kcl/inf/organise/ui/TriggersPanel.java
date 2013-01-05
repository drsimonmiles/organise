package uk.ac.kcl.inf.organise.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.data.Trigger;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventType;

public class TriggersPanel extends JPanel implements ActionListener {
    private final EventBus _bus;
    private final Database _database;
    private final JList _triggers;
    private final JButton _add, _remove;
    private final JPanel _buttons;
    
    public TriggersPanel (EventBus bus, Database database) {
        _bus = bus;
        _database = database;
        _triggers = new JList (new TriggerListModel (bus));
        _buttons = new JPanel ();
        _add = new JButton ("+");
        _remove = new JButton ("-");
        _buttons.add (_add);
        _buttons.add (_remove);
        
        setLayout (new BorderLayout ());
        add (_triggers, BorderLayout.CENTER);
        add (_buttons, BorderLayout.SOUTH);
        
        _add.addActionListener (this);
        _remove.addActionListener (this);
    }

    @Override
    public void actionPerformed (ActionEvent event) {
        OrganiseEvent focused = _bus.getMostRecent (OrganiseEventType.taskInFocus);
        OrganiseEvent anchored = _bus.getMostRecent (OrganiseEventType.anchorChanged);
        Trigger selected = (Trigger) _triggers.getSelectedValue ();
        Task anchor, current;
        
        if (event.getSource () == _add && focused != null && anchored != null && focused._task != null) {
            anchor = anchored._task;
            current = focused._task;
            current.addTrigger (new Trigger (anchor, current, _database));
        }
        if (event.getSource () == _remove && focused != null && selected != null && focused._task != null) {
            anchor = anchored._task;
            current = focused._task;
            for (Trigger trigger : current.getTriggers ()) {
                if (trigger.getTriggeringTasks ().contains (anchor)) {
                    current.removeTrigger (trigger);
                }
            }
        }
    }
}
