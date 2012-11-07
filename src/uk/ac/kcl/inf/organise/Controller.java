package uk.ac.kcl.inf.organise;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import uk.ac.kcl.inf.organise.access.AccessException;
import uk.ac.kcl.inf.organise.access.DatabaseLoader;
import uk.ac.kcl.inf.organise.access.DatabaseSaver;
import uk.ac.kcl.inf.organise.access.SummaryGenerator;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Priority;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.data.TaskUtils;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;
import uk.ac.kcl.inf.organise.events.OrganiseEventType;
import uk.ac.kcl.inf.organise.ui.MainWindow;
import uk.ac.kcl.inf.settings.OpenInstanceRenewer;

public class Controller implements OrganiseEventListener {
    private final Database _database;
    private final MainWindow _display;
    private final EventBus _bus;
    private final DatabaseLoader _loader;
    private final DatabaseSaver _saver;
    private final List<Task> _history;

    public Controller (Database data, MainWindow display, EventBus bus) throws AccessException {
        _database = data;
        _display = display;
        _history = new LinkedList<> ();

        if (!DatabaseLoader.isDatabaseDirectoryKnown ()) {
            DatabaseLoader.chooseDatabaseDirectory (display);
        }

        _bus = bus;
        _loader = new DatabaseLoader (_bus);
        _saver = new DatabaseSaver ();
        try {
            load ();
        } catch (AccessException cannotAccess) {
            DatabaseLoader.chooseDatabaseDirectory (display);
            load ();
        }

        _display.addKeyListener (bus);
        bus._listeners.add (this);
    }

    public void databaseFileUpdated () throws AccessException {
        load ();
    }

    public Database getDatabase () {
        return _database;
    }
    
    public void load () throws AccessException {
        _loader.loadDatabase (_database);
        if (_database.getProjectTasks (Database.EVENTS_PROJECT).isEmpty ()) {
            _database.addTask (new Task (Database.EVENTS_PROJECT, "", Priority.normal, "", true, _bus, _database));
        }
    }

    @Override
    public void organiseEvent (OrganiseEvent event) {
        try {
            switch (event._type) {
                case closing:
                    save ();
                    _saver.saveHistory (_history);
                    SummaryGenerator.generate (_database);
                    OpenInstanceRenewer.get ().close ();
                    break;
                case taskCompleted:
                    for (Task task : _database.getTasks ()) {
                        task.trigger (event._task);
                    }
                    break;
                case taskDeleted:
                    if (!event._task.getText ().equals ("")) {
                        _history.add (event._task);
                    }
                    break;
                case keyPressed:
                    if (event._keyEvent.isControlDown () && event._keyEvent.getKeyCode () == KeyEvent.VK_S) {
                        save ();
                    }
                    if (event._keyEvent.isControlDown () && event._keyEvent.getKeyCode () == KeyEvent.VK_Z) {
                        undo ();
                    }
                    break;
                case taskInFocus:
                    if (event._priorTask != null) {
                        _bus.event (OrganiseEventType.anchorChanged).task (event._priorTask).fire ();
                    }
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace ();
        }
    }

    public void run () {
        _bus.event (OrganiseEventType.opening).fire ();
        TaskUtils.triggerDates (_database);
        _display.setVisible (true);
    }

    public void save () throws IOException {
        _saver.save (_database);
    }
    
    private void undo () {
        if (_bus._undo.canUndo ()) {
            _bus._undo.undo ();
        }
    }
}
