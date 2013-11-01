package uk.ac.kcl.inf.organise.ui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEventType;
import uk.ac.kcl.inf.organise.ui.tree.TaskTreePanel;

public class MainWindow extends JFrame implements WindowListener {
    public static final String VERSION = "0.4";
    private final JTabbedPane _views;
    private final PrioritiesPanel _tasks;
    //private final TaskTreePanel _tree;
    private final ConfigurationPanel _config;
    private final TaskDetailPanel _details;
    private final EventBus _bus;

    public MainWindow (Database database, EventBus bus) {
        super ("Organise v" + VERSION);

        _bus = bus;
        _tasks = new PrioritiesPanel (database, bus);
        _details = new TaskDetailPanel (database, bus);
        //_tree = new TaskTreePanel (database, bus);
        _config = new ConfigurationPanel (bus);
        _views = new JTabbedPane ();
        
        _views.add ("Priorities", _tasks);
        _views.add ("Task", _details);
        //_views.add ("Relations", _tree);
        _views.add ("Configure", _config);
        add (_views);
        
        addWindowListener (this);
        setDefaultCloseOperation (EXIT_ON_CLOSE);
        setExtendedState (getExtendedState () | MAXIMIZED_BOTH);
    }

    @Override
    public void windowOpened (WindowEvent we) {
    }

    @Override
    public void windowClosing (WindowEvent we) {
        try {
            _bus.event (OrganiseEventType.closing).fire ();
        } catch (Throwable thrown) {
            thrown.printStackTrace ();
        }
    }

    @Override
    public void windowClosed (WindowEvent we) {
    }

    @Override
    public void windowIconified (WindowEvent we) {
    }

    @Override
    public void windowDeiconified (WindowEvent we) {
    }

    @Override
    public void windowActivated (WindowEvent we) {
    }

    @Override
    public void windowDeactivated (WindowEvent we) {
    }
}
