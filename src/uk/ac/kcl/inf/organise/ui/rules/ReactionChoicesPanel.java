package uk.ac.kcl.inf.organise.ui.rules;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Priority;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.rules.AllocateReaction;
import uk.ac.kcl.inf.organise.rules.DeleteTaskReaction;
import uk.ac.kcl.inf.organise.rules.PostponeRuleReaction;
import uk.ac.kcl.inf.organise.rules.Reaction;
import uk.ac.kcl.inf.organise.rules.RemoveRuleReaction;
import uk.ac.kcl.inf.organise.rules.SetPriorityReaction;

public class ReactionChoicesPanel extends JPanel implements ActionListener {
    private final JRadioButton _deleteTask, _setPriorityRed, _setPriorityAmber, _setPriorityGreen;
    private final JRadioButton _postpone, _remove, _allocate;
    private final JTextField _days, _minutes;
    private final JButton _add;
    private final RuleEditingPanel _parent;
    private final Database _database;
    private Task _task;

    public ReactionChoicesPanel (RuleEditingPanel parent, Database database) {
        ButtonGroup group = new ButtonGroup ();

        _parent = parent;
        _task = null;
        _database = database;
        
        _deleteTask = new JRadioButton ("Delete task");
        _setPriorityRed = new JRadioButton ("Set priority high");
        _setPriorityAmber = new JRadioButton ("Set priority medium");
        _setPriorityGreen = new JRadioButton ("Set priority low");
        _postpone = new JRadioButton ("Postpone rule (days)");
        _remove = new JRadioButton ("Remove rule");
        _allocate = new JRadioButton ("Allocate time (minutes)");
        _days = new JTextField ();
        _minutes = new JTextField ();
        _add = new JButton ("Add");
        
        group.add (_deleteTask);
        group.add (_setPriorityRed);
        group.add (_setPriorityAmber);
        group.add (_setPriorityGreen);
        group.add (_postpone);
        group.add (_remove);
        group.add (_allocate);
        _deleteTask.setSelected (true);

        setLayout (new MigLayout ("", "[0:0,grow 5,fill][0:0,grow 5,fill][0:0,grow 5,fill][0:0,grow 5,fill]", "[pref!][pref!][pref!][pref!]"));
        add (_deleteTask);
        add (_setPriorityRed);
        add (_setPriorityAmber);
        add (_setPriorityGreen, "wrap");
        add (_postpone);
        add (_days);
        add (_allocate);
        add (_minutes, "wrap");
        add (_remove, "wrap");
        add (_add, "span 4");
        
        _add.addActionListener (this);
    }

    @Override
    public void actionPerformed (ActionEvent occur) {
        Reaction reaction = null;
        
        if (_deleteTask.isSelected ()) {
            reaction = new DeleteTaskReaction (_task, _database);
        }
        if (_setPriorityRed.isSelected ()) {
            reaction = new SetPriorityReaction (_task, Priority.immediate);
        }
        if (_setPriorityAmber.isSelected ()) {
            reaction = new SetPriorityReaction (_task, Priority.urgent);
        }
        if (_setPriorityGreen.isSelected ()) {
            reaction = new SetPriorityReaction (_task, Priority.normal);
        }
        if (_postpone.isSelected ()) {
            try {
                reaction = new PostponeRuleReaction (Integer.parseInt (_days.getText ()));
            } catch (NumberFormatException notNumber) {
            }
        }
        if (_remove.isSelected ()) {
            reaction = new RemoveRuleReaction (_database);
        }
        if (_allocate.isSelected ()) {
            try {
                reaction = new AllocateReaction (_task, Integer.parseInt (_minutes.getText ()));
            } catch (NumberFormatException notNumber) {
            }
        }
        
        if (reaction != null) {
            _parent.addReaction (reaction);
        }
    }
    
    public void open (Task task) {
        _task = task;
    }
}
