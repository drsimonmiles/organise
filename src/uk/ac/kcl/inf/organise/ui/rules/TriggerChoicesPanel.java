package uk.ac.kcl.inf.organise.ui.rules;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class TriggerChoicesPanel extends JPanel {
    private final JRadioButton _completion, _onOrAfter, _dependent;
    private final JTextField _date, _task;
    private final JButton _add;

    public TriggerChoicesPanel () {
        ButtonGroup group = new ButtonGroup ();

        _completion = new JRadioButton ("Completion");
        _onOrAfter = new JRadioButton ("On or after (yyyy mm dd)");
        _dependent = new JRadioButton ("After completed");
        _date = new JTextField ();
        _task = new JTextField ();
        _add = new JButton ("Add");
        
        group.add (_completion);
        group.add (_onOrAfter);
        group.add (_dependent);

        setLayout (new MigLayout ("insets 0 0 0 0", "[0:0,grow 5,fill][0:0,grow 5,fill]", "[pref!][pref!][pref!][pref!]"));
        add (_completion, "wrap");
        add (_onOrAfter);
        add (_date, "wrap");
        add (_dependent);
        add (_task, "wrap");
        add (_add, "span 2");
        
        _dependent.setEnabled (false);
        _task.setEnabled (false);
    }
}
