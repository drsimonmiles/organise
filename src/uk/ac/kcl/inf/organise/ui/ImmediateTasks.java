package uk.ac.kcl.inf.organise.ui;

import java.util.List;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Priority;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;

public class ImmediateTasks extends TaskList implements OrganiseEventListener {
    private final Database _database;
    private final EventBus _bus;
    private final Priority _priority;

    public ImmediateTasks (Priority priority, Database database, EventBus bus) {
        super (false, false, true, database, bus);
        _priority = priority;
        _database = database;
        _bus = bus;
        _bus._listeners.add (this);
        tasksAdded (database.getPriorityTasks (priority));
    }

    @Override
    protected void addTask (Task task, Task follow) {
        throw new RuntimeException ("Adding not supported on immediate lists");
    }

    @Override
    protected Task createTask (String text) {
        throw new RuntimeException ("Task creation not supported on immediate lists");
    }

    @Override
    protected List<Task> getTasks () {
        return _database.getPriorityTasks (_priority);
    }

    @Override
    public void organiseEvent (OrganiseEvent event) {
        switch (event._type) {
            case projectAdded:
                for (Task task : _database.getProjectTasks (event._project)) {
                    if (task.getPriority () == _priority) {
                        taskAdded (task);
                    }
                }
                break;
            case projectDeleted:
                for (Task task : _database.getProjectTasks (event._project)) {
                    if (task.getPriority () == _priority) {
                        taskRemoved (task);
                    }
                }
                break;
            case taskAdded:
                if (event._task.getPriority () == _priority) {
                    taskAdded (event._task);
                }
                break;
            case taskDeleted:
                if (event._task.getPriority () == _priority) {
                    taskRemoved (event._task);
                }
                break;
            case taskPriorityChanged:
                if (event._task.getPriority () == _priority) {
                    taskAdded (event._task);
                } else {
                    if (event._priorPriority == _priority) {
                        taskRemoved (event._task);
                    }
                }
                break;
            case taskOrderChanged:
                if (event._task.getPriority ().equals (_priority)) {
                    taskMoved (event._task);
                }
                break;
        }
    }
}
