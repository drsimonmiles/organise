package uk.ac.kcl.inf.organise.ui;

import java.util.List;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Priority;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;

public class ProjectTasks extends TaskList implements OrganiseEventListener {
    private String _project;
    private final Database _database;
    private final EventBus _bus;

    public ProjectTasks (String project, Database database, EventBus bus) {
        super (true, true, false, database, bus);
        _project = project;
        _database = database;
        _bus = bus;
        tasksAdded (_database.getProjectTasks (_project));
        bus._listeners.add (this);
    }

    @Override
    protected void addTask (Task newTask, Task followTask) {
        _database.addTask (newTask, followTask);
    }

    @Override
    protected Task createTask (String text) {
        return new Task (_project, text, Priority.normal, "", false, _bus, _database);
    }

    public String getProject () {
        return _project;
    }
    
    @Override
    protected List<Task> getTasks () {
        return _database.getProjectTasks (_project);
    }
    
    @Override
    public void organiseEvent (OrganiseEvent event) {
        switch (event._type) {
            case projectRenamed:
                if (event._priorString.equals (_project)) {
                    _project = event._project;
                }
                break;
            case taskAdded:
                if (event._task.getProject ().equals (_project)) {
                    taskAdded (event._task);
                }
                break;
            case taskDeleted:
                if (event._task.getProject ().equals (_project)) {
                    taskRemoved (event._task);
                }
                break;
            case taskOrderChanged:
                if (event._task.getProject ().equals (_project)) {
                    taskMoved (event._task);
                }
                break;
        }
    }
    
    public String toString () {
        return _project;
    }
}
