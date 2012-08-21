package uk.ac.kcl.inf.organise.ui.tree;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreeNode;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;

public abstract class TaskTreeNode implements OrganiseEventListener, TreeNode {
    protected final TaskTreeNode _parent;
    protected final TaskTreeModel _model;
    protected final Database _database;
    protected final EventBus _bus;    
    protected final Map<Object, TaskTreeNode> _branches;
    
    public TaskTreeNode (TaskTreeNode parent, TaskTreeModel model, Database database, EventBus bus) {
        _parent = parent;
        _model = model;
        _database = database;
        _branches = new HashMap <> ();
        _bus = bus;
        _bus._listeners.add (this);
    }
    
    @Override
    public Enumeration children () {
        return new Enumeration () {
            private int _index = 0;

            @Override
            public boolean hasMoreElements () {
                return _index < getChildCount () - 1;
            }

            @Override
            public Object nextElement () {
                Object next = getChildAt (_index);
                _index += 1;
                return next;
            }
        };
    }
    
    @Override
    public boolean equals (Object other) {
        return other != null && other instanceof TaskTreeNode
               && ((TaskTreeNode) other).getValue ().equals (getValue ())
               && ((((TaskTreeNode) other).getParent ()  == null && getParent ()  == null)
                   || (getParent () != null && ((TaskTreeNode) other).getParent ()  != null && ((TaskTreeNode) other).getParent ().equals (getParent ())));
    }

    protected void fireAdd (TreeModelEvent event) {
        _model.fireAdd (event);
    }
    
    protected void fireChange (TreeModelEvent event) {
        _model.fireChange (event);
    }
    
    protected void fireRemove (TreeModelEvent event) {
        _model.fireRemove (event);
    }

    protected void fireRestructure (TreeModelEvent event) {
        _model.fireRestructure (event);
    }
    
    protected TaskTreeNode getBranch (Object child) {
        TaskTreeNode branch = _branches.get (child);
        
        if (branch == null) {        
            branch = createChildBranch (child);
            _branches.put (child, branch);
        }
        
        return branch;
    }

    protected abstract List<? extends Object> getChildren ();

    @Override
    public TreeNode getChildAt (int childIndex) {
        return getBranch (getChildren ().get (childIndex));
    }
    
    protected abstract TaskTreeNode createChildBranch (Object child);
    
    @Override
    public int getChildCount () {
        return getChildren ().size ();
    }

    public List<Object> getPath () {
        List<Object> path = new LinkedList<> ();

        if (_parent == null) {
            path.add (this);
        } else {
            path.addAll (_parent.getPath ());
            path.add (this);
        }

        return path;
    }
    
    public Object[] getPathArray () {
        return getPath ().toArray ();
    }

    @Override
    public TreeNode getParent () {
        return _parent;
    }

    @Override
    public int getIndex (TreeNode node) {
        return getChildren ().indexOf (((TaskTreeNode) node).getValue ());
    }

    @Override
    public boolean getAllowsChildren () {
        return true;
    }
    
    public abstract Object getValue ();

    public abstract void handleCommand (String command);
    
    @Override
    public int hashCode () {
        return getValue ().hashCode ();
    }
    
    @Override
    public boolean isLeaf () {
        return false;
    }
    
    @Override
    public String toString () {
        return getValue ().toString ();
    }
}
