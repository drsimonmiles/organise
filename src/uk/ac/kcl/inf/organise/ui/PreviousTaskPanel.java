package uk.ac.kcl.inf.organise.ui;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;

public class PreviousTaskPanel extends JTextField implements DocumentListener, OrganiseEventListener {
    private Task _anchor;
    private boolean _changing;

    public PreviousTaskPanel (Task initial, EventBus bus) {
        open (initial);
        getDocument ().addDocumentListener (this);
        addKeyListener (bus);
        _changing = false;
        bus._listeners.add (this);
        getDocument ().addUndoableEditListener (bus._undo);
    }

    public PreviousTaskPanel (EventBus bus) {
        this (null, bus);
    }

    @Override
    public void changedUpdate (DocumentEvent event) {
        documentEvent (event);
    }

    private void documentEvent (DocumentEvent event) {
        _changing = true;
        _anchor.setText (getText ());
        _changing = false;
    }

    @Override
    public void insertUpdate (DocumentEvent event) {
        documentEvent (event);
    }

    public void open (Task task) {
        if (task != null) {
            _anchor = task;
            if (_anchor == null) {
                setText ("");
                setEnabled (false);
            } else {
                setText (_anchor.getText ());
                setEnabled (true);
            }
            repaint ();
        }
    }

    @Override
    public void organiseEvent (OrganiseEvent event) {
        switch (event._type) {
            case anchorChanged:
                open (event._task);
                break;
            case taskTextChanged:
                if (_anchor != null && _anchor.equals (event._task) && !_changing) {
                    setText (_anchor.getText ());
                    repaint ();
                }
                break;
        }
    }

    @Override
    public void removeUpdate (DocumentEvent event) {
        documentEvent (event);
    }
}
