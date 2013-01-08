package uk.ac.kcl.inf.organise.ui.rules;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class ItemListPanel <T> extends JPanel {
    private final boolean _deleteButtons, _editButtons;
    private final List<ItemsListener> _listeners;
    private final Map<T, ItemPanel> _panels;
    private final List<T> _items;
    
    public ItemListPanel (boolean deleteButtons, boolean editButtons) {
        setLayout (new MigLayout ("insets 0 0 0 0", "0[grow,fill]0", ""));
        _deleteButtons = deleteButtons;
        _editButtons = editButtons;
        _listeners = new LinkedList<> ();
        _panels = new HashMap<> ();
        _items = new LinkedList<> ();
    }
    
    public void addItem (T item) {
        ItemPanel panel = new ItemPanel (item, _deleteButtons, _editButtons, this);
        
        _items.add (item);
        _panels.put (item, panel);
        add (panel, "wrap");
        validate ();
        repaint ();
    }
    
    public void addItemsListener (ItemsListener listener) {
        _listeners.add (listener);
    }
    
    public void clear () {
        for (T item : _items){
            remove (_panels.get (item));
        }
        _items.clear ();
        _panels.clear ();
        validate ();
        repaint ();
    }
    
    public void delete (T item) {
        removeItem (item);
        for (ItemsListener listener : _listeners) {
            listener.delete (item, this);
        }
    }

    public void edit (T item) {
        for (ItemsListener listener : _listeners) {
            listener.edit (item, this);
        }
    }
    
    public List<T> getItems () {
        return _items;
    }
    
    public void removeItem (T item) {
        ItemPanel panel = _panels.get (item);
        
        if (panel != null) {
            remove (panel);
        }
        _items.remove (item);
        _panels.remove (item);
        validate ();
        repaint ();
    }
}
