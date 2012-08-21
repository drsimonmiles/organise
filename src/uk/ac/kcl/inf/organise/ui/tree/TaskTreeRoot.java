package uk.ac.kcl.inf.organise.ui.tree;

import java.util.List;
import javax.swing.event.TreeModelEvent;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;

public class TaskTreeRoot extends TaskTreeNode {
    public TaskTreeRoot (TaskTreeModel model, Database database, EventBus bus) {
        super (null, model, database, bus);
    }

    @Override
    protected ProjectBranch createChildBranch (Object project) {
        return new ProjectBranch ((String) project, this, _model, _database, _bus);
    }

    @Override
    protected List<? extends Object> getChildren () {
        return _database.getProjects ();
    }

    @Override
    public Object getValue () {
        return "Be organised";
    }

    public void handleCommand (String command) {
    }
    
    @Override
    public void organiseEvent (OrganiseEvent event) {
        TaskTreeNode branch;
        int[] indices;
        
        switch (event._type) {
            case projectAdded:
                branch = getBranch (event._project);
                indices = new int[] {getChildren ().indexOf (event._project)};
                fireAdd (new TreeModelEvent (_model, getPathArray (), indices, new Object[] {branch}));
                break;
            case projectDeleted:
                fireRestructure (new TreeModelEvent (_model, getPathArray ()));
                break;
        }
    }
}
