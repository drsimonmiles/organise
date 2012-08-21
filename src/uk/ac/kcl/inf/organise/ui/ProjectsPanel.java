package uk.ac.kcl.inf.organise.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Priority;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;

public class ProjectsPanel extends JTabbedPane implements ChangeListener, OrganiseEventListener {
    private final List<JScrollPane> _order;
    private final Map<String, JScrollPane> _byName;
    private final Database _database;
    private final EventBus _bus;
    private boolean _adding;

    public ProjectsPanel (Database database, EventBus bus) {
        super (JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        _adding = false;
        _database = database;
        _bus = bus;
        _order = new LinkedList<> ();
        _byName = new HashMap<> ();
        add ("+", new JPanel ());
        for (String project : database.getProjects ()) {
            addProject (project);
        }
        setSelectedIndex (0);
        addChangeListener (this);
        bus._listeners.add (this);
    }

    /**
     * Create a new project tasks panel and add a tab, for the given project.
     * @param project The project to add
     */
    public void addProject (String title) {
        ProjectTasks tasks = new ProjectTasks (title, _database, _bus);
        JScrollPane scroll = new JScrollPane (tasks, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        int index;

        if (_order.isEmpty () || title.equals ("+")) {
            index = getTabCount () - 1;
        } else {
            index = -Collections.binarySearch (_order, tasks, SortPanelByProjectID.get ()) - 1;
        }
        _adding = true;
        _order.add (index, scroll);
        insertTab (title, null, scroll, title, index);
        setTabComponentAt (index, new ProjectTab (this, title, scroll, _database, _bus));
        _byName.put (title, scroll);
        _adding = false;
    }
    
    @Override
    public void organiseEvent (OrganiseEvent event) {
        JScrollPane panel;

        switch (event._type) {
            case opening:
                setSelectedIndex (0);
                break;
            case projectAdded:
                if (!event._project.equals (Database.EVENTS_PROJECT)) {
                    addProject (event._project);
                }
                break;
            case projectDeleted:
                System.out.println (event._project);
                setSelectedIndex (0);
                panel = _byName.get (event._project);
                remove (panel);
                _byName.remove (event._project);
                _order.remove (panel);
                setSelectedIndex (0);
                break;
            case taskAdded:
                validate ();
                repaint ();
                break;
        }
    }

    @Override
    public void stateChanged (ChangeEvent event) {
        if (getSelectedIndex () == getTabCount () - 1 && !_adding) {
            String name = JOptionPane.showInputDialog (this, "Project name", "Enter new project name", JOptionPane.QUESTION_MESSAGE);

            _database.addTask (new Task (name, "", Priority.normal, "", false, _bus, _database));
            setSelectedIndex (getTabCount () - 2);
        }
    }
}
