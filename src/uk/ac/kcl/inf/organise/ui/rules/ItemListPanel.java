package uk.ac.kcl.inf.organise.ui.rules;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class ItemListPanel extends JPanel {
    private final boolean _deleteButtons, _editButtons;
    private final List<ItemsListener> _listeners;
    private final Map<Object, ItemPanel> _panels;
    
    public ItemListPanel (boolean deleteButtons, boolean editButtons) {
        setLayout (new MigLayout ("insets 0 0 0 0", "0[grow,fill]0", ""));
        _deleteButtons = deleteButtons;
        _editButtons = editButtons;
        _listeners = new LinkedList<> ();
        _panels = new HashMap<> ();
    }
    
    public void addItem (Object item) {
        ItemPanel panel = new ItemPanel (item, _deleteButtons, _editButtons, this);
        
        _panels.put (item, panel);
        add (panel, "wrap");
    }
    
    public void addItemsListener (ItemsListener listener) {
        _listeners.add (listener);
    }
    
    public void delete (Object item) {
        remove (_panels.get (item));
        for (ItemsListener listener : _listeners) {
            listener.delete (item, this);
        }
    }

    public void edit (Object item) {
        for (ItemsListener listener : _listeners) {
            listener.edit (item, this);
        }
    }
}
