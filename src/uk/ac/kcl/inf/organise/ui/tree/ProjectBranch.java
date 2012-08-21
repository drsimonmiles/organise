package uk.ac.kcl.inf.organise.ui.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreeNode;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;

public class ProjectBranch extends TaskTreeNode {
    private String _project;

    public ProjectBranch (String project, TaskTreeNode parent, TaskTreeModel model, Database database, EventBus bus) {
        super (parent, model, database, bus);
        _project = project;
    }
    
    @Override
    protected TaskTreeNode createChildBranch (Object child) {
        return new TaskBranch ((Task) child, this, _model, _database, _bus);
    }

    @Override
    protected List<? extends Object> getChildren () {
        return _database.getProjectTasks (_project);
    }

    @Override
    public Object getValue () {
        return _project;
    }
    
    public void handleCommand (String command) {
    }

    @Override
    public void organiseEvent (OrganiseEvent event) {
        switch (event._type) {
            case taskAdded:
            case taskDeleted:
                if (event._project != null && event._project.equals (_project)) {
                    fireRestructure (new TreeModelEvent (_model, getPathArray ()));
                }
                break;
        }
    }
}
