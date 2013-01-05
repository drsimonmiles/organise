package uk.ac.kcl.inf.organise.ui;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import net.miginfocom.swing.MigLayout;
import uk.ac.kcl.inf.organise.SettingType;
import uk.ac.kcl.inf.organise.access.SummaryGenerator;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;
import uk.ac.kcl.inf.settings.Settings;

public class PrioritiesPanel extends JPanel implements ActionListener, OrganiseEventListener {
    private final ProjectsPanel _projects;
    private final TopPanel _top;
    private final JPanel _buttons;
    private final JSplitPane _split;
    private final JButton _undo, _summary;
    private final Database _database;
    private final EventBus _bus;

    public PrioritiesPanel (Database database, EventBus bus) {
        _database = database;
        _bus = bus;
        _projects = new ProjectsPanel (database, bus);
        _top = new TopPanel (database, bus);
        _split = new JSplitPane (JSplitPane.VERTICAL_SPLIT, _top, _projects);
        _buttons = new JPanel ();
        _undo = new JButton ("Undo");
        _summary = new JButton ("Summary");

        _undo.setMargin (new Insets (0, 0, 0, 0));
        _summary.setMargin (new Insets (0, 0, 0, 0));
        _buttons.setLayout (new FlowLayout ());
        _buttons.add (_undo);
        _buttons.add (_summary);
        setLayout (new MigLayout ("", "[0:0,grow 100,fill]", "[0:0,grow,fill]0[min!]0"));
        add (_split, "wrap");
        add (_buttons, "shrinky");
        _undo.addActionListener (this);
        _summary.addActionListener (this);
        Settings.get ().loadPosition (SettingType.mainWindowSplit, _split);
        _bus._listeners.add (this);
    }

    @Override
    public void actionPerformed (ActionEvent occur) {
        switch (occur.getActionCommand ()) {
            case "Undo":
                undo ();
                break;
            case "Summary":
                SummaryGenerator.generate (_database);
                break;
        }
    }

    @Override
    public void organiseEvent (OrganiseEvent event) {
        switch (event._type) {
            case closing:
                Settings.get ().savePosition (SettingType.mainWindowSplit, _split);
                break;
        }
    }

    private void undo () {
        if (_bus._undo.canUndo ()) {
            _bus._undo.undo ();
        }
    }
}
