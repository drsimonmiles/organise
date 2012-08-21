package uk.ac.kcl.inf.organise.ui.tree;

import java.util.List;
import javax.swing.event.TreeModelEvent;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.data.Trigger;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;

public class TaskBranch extends TaskTreeNode implements OrganiseEventListener {
    public final Task _task;

    public TaskBranch (Task task, TaskTreeNode parent, TaskTreeModel model, Database database, EventBus bus) {
        super (parent, model, database, bus);
        _task = task;
    }

    @Override
    protected TaskTreeNode createChildBranch (Object child) {
        return new TriggerBranch ((Trigger) child, this, _model, _database, _bus);
    }

    @Override
    protected List<? extends Object> getChildren () {
        return _task.getTriggers ();
    }

    public Object getValue () {
        return _task;
    }

    public void handleCommand (String command) {
        switch (command) {
            case TaskTreePanel.ADD_AS_TRIGGERING:
            case TaskTreePanel.REMOVE_AS_TRIGGERING:
            case TaskTreePanel.IMMEDIATE_IF_ANY_OF:
            case TaskTreePanel.URGENT_IF_ALL_OF:
            case TaskTreePanel.URGENT_IF_ANY_OF:
            case TaskTreePanel.COMPLETE_IF_ALL_OF:
            case TaskTreePanel.COMPLETE_IF_ANY_OF:
        }
    }

    @Override
    public void organiseEvent (OrganiseEvent event) {
        switch (event._type) {
            case taskChildAdded:
            case taskChildRemoved:
                if (event._task.equals (_task)) {
                    fireRestructure (new TreeModelEvent (_model, getPathArray ()));
                }
                break;
        }
    }
}
