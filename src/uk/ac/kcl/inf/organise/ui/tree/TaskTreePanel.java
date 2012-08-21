package uk.ac.kcl.inf.organise.ui.tree;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.events.EventBus;

public class TaskTreePanel extends JPanel implements ActionListener, MouseListener, TreeSelectionListener {
    public static final String ADD_AS_TRIGGERING = "Add as triggering";
    public static final String REMOVE_AS_TRIGGERING = "Remove as triggering";
    public static final String IMMEDIATE_IF_ANY_OF = "Immediate if ANY of";
    public static final String IMMEDIATE_IF_ALL_OF = "Immediate if ALL of";
    public static final String URGENT_IF_ANY_OF = "Urgent if ANY of";
    public static final String URGENT_IF_ALL_OF = "Urgent if ALL of";
    public static final String COMPLETE_IF_ANY_OF = "Complete if ANY of";
    public static final String COMPLETE_IF_ALL_OF = "Complete if ALL of";
            
    private final JTree _tree;
    private final TaskTreeModel _model;
    private final JPopupMenu _triggerContext, _taskContext, _triggeringContext;
    private TreePath[] _current;
    private TreePath _menuPoint;

    public TaskTreePanel (Database database, EventBus bus) {
        _model = new TaskTreeModel (database, bus);
        _tree = new JTree (_model);

        setLayout (new BorderLayout ());
        add (new JScrollPane (_tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

        _current = null;

        _triggerContext = new JPopupMenu ();
        _taskContext = new JPopupMenu ();
        _triggeringContext = new JPopupMenu ();
        addItems (_triggerContext, this, ADD_AS_TRIGGERING);
        addItems (_taskContext, this, IMMEDIATE_IF_ANY_OF, IMMEDIATE_IF_ALL_OF, URGENT_IF_ANY_OF,
                  URGENT_IF_ALL_OF, COMPLETE_IF_ANY_OF, COMPLETE_IF_ALL_OF);
        addItems (_triggeringContext, this, REMOVE_AS_TRIGGERING, IMMEDIATE_IF_ANY_OF, IMMEDIATE_IF_ALL_OF, URGENT_IF_ANY_OF,
                  URGENT_IF_ALL_OF, COMPLETE_IF_ANY_OF, COMPLETE_IF_ALL_OF);

        _tree.addTreeSelectionListener (this);
        _tree.addMouseListener (this);
    }
    
    @Override
    public void actionPerformed (ActionEvent occur) {
        ((TaskTreeNode) _menuPoint.getLastPathComponent ()).handleCommand (occur.getActionCommand ());
    }
    
    private static void addItems (JPopupMenu menu, ActionListener listener, String... titles) {
        JMenuItem item;

        for (String title : titles) {
            item = new JMenuItem (title);
            item.addActionListener (listener);
            menu.add (item);
        }
    }

    private void maybeShowPopup (MouseEvent occur) {
        JPopupMenu relevant = null;
        Object last;

        _menuPoint = _tree.getClosestPathForLocation (occur.getX (), occur.getY ());
        last = _menuPoint.getLastPathComponent ();
        
        if (last instanceof TaskBranch) {
            relevant = _taskContext;
        }
        if (last instanceof TriggerBranch) {
            relevant = _triggerContext;
        }
        if (relevant == null) {
            return;
        }
        if (occur.isPopupTrigger ()) {
            relevant.show (occur.getComponent (), occur.getX (), occur.getY ());
        }
    }
    
    @Override
    public void mouseClicked (MouseEvent occur) {
    }

    @Override
    public void mouseEntered (MouseEvent occur) {
    }

    @Override
    public void mouseExited (MouseEvent occur) {
    }

    @Override
    public void mousePressed (MouseEvent occur) {
        maybeShowPopup (occur);
    }

    @Override
    public void mouseReleased (MouseEvent occur) {
        maybeShowPopup (occur);
    }

    @Override
    public void valueChanged (TreeSelectionEvent event) {
        _current = _tree.getSelectionModel ().getSelectionPaths ();
    }
}