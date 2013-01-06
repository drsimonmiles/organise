package uk.ac.kcl.inf.organise.ui.rules;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import uk.ac.kcl.inf.organise.access.DatabaseLoader;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.rules.CompletionTrigger;
import uk.ac.kcl.inf.organise.rules.OnOrAfterTrigger;
import uk.ac.kcl.inf.organise.rules.Trigger;

public class TriggerChoicesPanel extends JPanel implements ActionListener {
    private final JRadioButton _completion, _onOrAfter, _dependent;
    private final JTextField _date, _dependentTask;
    private final JButton _add;
    private final RuleEditingPanel _parent;
    private final EventBus _bus;
    private Task _task;

    public TriggerChoicesPanel (RuleEditingPanel parent, EventBus bus) {
        ButtonGroup group = new ButtonGroup ();

        _parent = parent;
        _task = null;
        _bus = bus;
        
        _completion = new JRadioButton ("Completion");
        _onOrAfter = new JRadioButton ("On or after (yyyy mm dd)");
        _dependent = new JRadioButton ("After completed");
        _date = new JTextField ();
        _dependentTask = new JTextField ();
        _add = new JButton ("Add");
        
        group.add (_completion);
        group.add (_onOrAfter);
        group.add (_dependent);
        _completion.setSelected (true);

        setLayout (new MigLayout ("insets 0 0 0 0", "[0:0,grow 5,fill][0:0,grow 5,fill]", "[pref!][pref!][pref!][pref!]"));
        add (_completion, "wrap");
        add (_onOrAfter);
        add (_date, "wrap");
        add (_dependent);
        add (_dependentTask, "wrap");
        add (_add, "span 2");
        
        _dependent.setEnabled (false);
        _dependentTask.setEnabled (false);
        
        _add.addActionListener (this);
    }

    @Override
    public void actionPerformed (ActionEvent occur) {
        Trigger trigger = null;
        
        if (_completion.isSelected ()) {
            trigger = new CompletionTrigger (_task, _bus);
        }
        if (_onOrAfter.isSelected ()) {
            try {
                trigger = new OnOrAfterTrigger (DatabaseLoader.DATE_FORMAT.parse (_date.getText ()), _bus);
            } catch (ParseException notADate) {
            }
        }
        
        if (trigger != null) {
            _parent.addTrigger (trigger);
        }
    }
    
    public void open (Task task) {
        _task = task;
    }
}
