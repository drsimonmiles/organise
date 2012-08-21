package uk.ac.kcl.inf.organise.ui.tree;

import java.util.List;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.data.Trigger;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;

public class TriggerBranch extends TaskTreeNode {
    private Trigger _trigger;
    
    public TriggerBranch (Trigger trigger, TaskTreeNode parent, TaskTreeModel model, Database database, EventBus bus) {
        super (parent, model, database, bus);
        _trigger = trigger;
    }

    @Override
    protected TaskTreeNode createChildBranch (Object child) {
        return new TaskBranch ((Task) child, this, _model, _database, _bus);
    }

    @Override
    protected List<? extends Object> getChildren () {
        return _trigger.getTriggeringTasks ();
    }

    @Override
    public Object getValue () {
        return _trigger;
    }

    public void handleCommand (String command) {
    }    
    
    @Override
    public void organiseEvent (OrganiseEvent event) {
    }
}
