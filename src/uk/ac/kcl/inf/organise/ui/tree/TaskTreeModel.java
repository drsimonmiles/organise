package uk.ac.kcl.inf.organise.ui.tree;

import java.util.LinkedList;
import java.util.List;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.events.EventBus;

public class TaskTreeModel implements TreeModel {
    private final TaskTreeRoot _root;
    private final List<TreeModelListener> _listeners;

    public TaskTreeModel (Database database, EventBus bus) {
        _root = new TaskTreeRoot (this, database, bus);
        _listeners = new LinkedList<> ();
    }

    @Override
    public void addTreeModelListener (TreeModelListener newListener) {
        _listeners.add (newListener);
    }

    public void fireAdd (TreeModelEvent event) {
        for (TreeModelListener listener : _listeners) {
            try {
                listener.treeNodesInserted (event);
            } catch (Throwable cannotAdd) {
                System.err.println (cannotAdd.getMessage ());
            }
        }
    }

    public void fireChange (TreeModelEvent event) {
        for (TreeModelListener listener : _listeners) {
            listener.treeNodesChanged (event);
        }
    }

    public void fireRemove (TreeModelEvent event) {
        for (TreeModelListener listener : _listeners) {
            listener.treeNodesRemoved (event);
        }
    }

    public void fireRestructure (TreeModelEvent event) {
        for (TreeModelListener listener : _listeners) {
            try {
                listener.treeStructureChanged (event);
            } catch (Throwable cannotRestructure) {
                System.err.println (cannotRestructure.getMessage ());
            }
        }
    }

    @Override
    public Object getRoot () {
        return _root;
    }

    @Override
    public Object getChild (Object parent, int index) {
        return ((TaskTreeNode) parent).getChildAt (index);
    }

    @Override
    public int getChildCount (Object parent) {
        return ((TaskTreeNode) parent).getChildCount ();
    }

    @Override
    public int getIndexOfChild (Object parent, Object child) {
        return ((TaskTreeNode) parent).getIndex ((TaskTreeNode) child);
    }

    @Override
    public boolean isLeaf (Object node) {
        return ((TaskTreeNode) node).isLeaf ();
    }

    @Override
    public void removeTreeModelListener (TreeModelListener oldListener) {
        _listeners.remove (oldListener);
    }

    @Override
    public void valueForPathChanged (TreePath path, Object newValue) {
    }
}
