package uk.ac.kcl.inf.organise.ui.rules;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class ReactionChoicesPanel extends JPanel {
    private final JRadioButton _deleteTask, _setPriorityRed, _setPriorityAmber, _setPriorityGreen;
    private final JRadioButton _postpone, _remove, _allocate;
    private final JTextField _days, _minutes;
    private final JButton _add;

    public ReactionChoicesPanel () {
        ButtonGroup group = new ButtonGroup ();

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

        setLayout (new MigLayout ("insets 0 0 0 0", "[0:0,grow 5,fill][0:0,grow 5,fill]", "[pref!][pref!][pref!][pref!]"));
        add (_deleteTask, "wrap");
        add (_setPriorityRed, "wrap");
        add (_setPriorityAmber, "wrap");
        add (_setPriorityGreen, "wrap");
        add (_postpone);
        add (_days, "wrap");
        add (_remove, "wrap");
        add (_allocate);
        add (_minutes, "wrap");
        add (_add, "span 2");
    }

}
