package uk.ac.kcl.inf.organise.ui.rules;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import uk.ac.kcl.inf.organise.ui.DeleteIcon;

public class ItemPanel <T> extends JPanel implements ActionListener {
    private final T _item;
    private final JTextField _text;
    private final JButton _delete, _edit;
    private final ItemListPanel _list;
    
    public ItemPanel (T item, boolean deleteButton, boolean editButton, ItemListPanel list) {
        _item = item;
        _text = new JTextField (_item.toString ());
        _delete = new JButton (new DeleteIcon ());
        _edit = new JButton ("Edit");
        _list = list;

        if (deleteButton || editButton) {
            if (deleteButton && editButton) {
                setLayout (new MigLayout ("", "[0:0,grow 10,fill][0:0,grow 1,fill][0:0,grow 1,fill]", "0[pref!]0"));
            } else {
                setLayout (new MigLayout ("", "[0:0,grow 10,fill][0:0,grow 1,fill]", "0[pref!]0"));
            }
        } else {
            setLayout (new MigLayout ("", "[0:0,grow,fill]", "0[pref!]0"));
        }
        
        _text.setEditable (false);
        _text.setBackground (Color.white);
        
        add (_text);
        if (deleteButton) {
            add (_delete);
            _delete.addActionListener (this);
        }
        if (editButton) {
            add (_edit);
            _edit.addActionListener (this);
        }
    }

    @Override
    public void actionPerformed (ActionEvent pressed) {
        if (pressed.getSource () == _delete) {
            _list.delete (_item);
        }
        if (pressed.getSource () == _edit) {
            _list.edit (_item);
        }
    }
}
