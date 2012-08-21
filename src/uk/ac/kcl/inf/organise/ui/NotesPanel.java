package uk.ac.kcl.inf.organise.ui;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;

public class NotesPanel extends JTextArea implements DocumentListener, OrganiseEventListener {
    private Task _task;
    private boolean _changing;
    
    public NotesPanel (Task initial, EventBus bus) {
        open (initial);
        getDocument ().addDocumentListener (this);
        addKeyListener (bus);
        _changing = false;
        bus._listeners.add (this);
        getDocument ().addUndoableEditListener (bus._undo);
    }
    
    public NotesPanel (EventBus bus) {
        this (null, bus);
    }
    
    @Override
    public void changedUpdate (DocumentEvent event) {
        documentEvent (event);
    }

    private void documentEvent (DocumentEvent event) {
        _changing = true;
        _task.setNotes (getText ());
        _changing = false;        
    }
    
    @Override
    public void insertUpdate (DocumentEvent event) {
        documentEvent (event);
    }

    public void open (Task task) {
        _task = task;
        if (task == null) {
            setText ("");
            setEnabled (false);
        } else {
            setText (_task.getNotes ());
            setEnabled (true);
        }
    }
    
    @Override
    public void organiseEvent (OrganiseEvent event) {
        switch (event._type) {
            case taskInFocus:
                open (event._task);
                break;
            case taskNotesChanged:
                if (_task != null && _task.equals (event._task) && !_changing) {
                    setText (_task.getNotes ());
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
