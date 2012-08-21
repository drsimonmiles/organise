package uk.ac.kcl.inf.organise.access;

import java.util.LinkedList;
import java.util.List;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Priority;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.data.Trigger;
import uk.ac.kcl.inf.organise.events.EventBus;

public class DatabaseLoaderV02 {
    public static final String DATABASE_FILE = "database.xml";
    public static final String NORMAL_PRIORITY = "NORMAL";
    public static final String URGENT_PRIORITY = "URGENT";
    public static final String IMMEDIATE_PRIORITY = "IMMEDIATE";
    private final EventBus _bus;

    public DatabaseLoaderV02 (EventBus bus) {
        _bus = bus;
    }

    public void loadDatabase (Database database, Document document) throws AccessException {
        List<Integer> triggers = new LinkedList<> ();
        
        loadTasks (database, document.query ("/database/task"), triggers);
        loadTriggers (database, triggers);
    }

    private void loadTasks (Database database, Nodes elements, List<Integer> triggers) {
        for (int index = 0; index < elements.size (); index += 1) {
            loadTask (database, elements.get (index), triggers);
        }
    }

    private void loadTask (Database database, Node element, List<Integer> triggers) {
        String text = DatabaseLoader.getText (element, "text/text()", false);
        String project = DatabaseLoader.getText (element, "project/text()", false);
        Priority priority = DatabaseLoader.getPriority (element, "priority/text()");
        int triggerID = DatabaseLoader.getInteger (element, "trigger/text()");
        String notes = DatabaseLoader.getText (element, "notes/text()", false);
        boolean triggerOnly = DatabaseLoader.getBoolean (element, "triggerOnly/text()");
        Task task = new Task (project, text, priority, notes, triggerOnly, _bus, database);

        database.addTask (task);
        triggers.add (triggerID);
    }

    private void loadTriggers (Database database, List<Integer> triggers) {
        List<Task> tasks = database.getTasks ();
        Task task;
        int trigger;
        
        for (int index = 0; index < triggers.size (); index += 1) {
            task = tasks.get (index);
            trigger = triggers.get (index);
            if (trigger >= 0 && tasks.size () > trigger) {
                task.addTrigger (new Trigger (tasks.get (trigger), task, database));
            }
        }
    }
}
