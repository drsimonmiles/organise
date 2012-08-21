package uk.ac.kcl.inf.organise.events;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.undo.UndoManager;
import static uk.ac.kcl.inf.organise.events.OrganiseEventType.*;

public class EventBus implements KeyListener {
    public final List<OrganiseEventListener> _listeners;
    public final Map<OrganiseEventType, OrganiseEvent> _mostRecent;
    public final UndoManager _undo;
        
    public EventBus () {
        _listeners = new LinkedList <> ();
        _mostRecent = new HashMap <> ();
        _undo = new UndoManager ();
    }

    public OrganiseEvent event (OrganiseEventType type) {
        return new OrganiseEvent (type, this);
    }
    
    void fire (OrganiseEvent event) {
        for (OrganiseEventListener listener : new LinkedList<> (_listeners)) {
            listener.organiseEvent (event);
        }
        _mostRecent.put (event._type, event);
    }
    
    public OrganiseEvent getMostRecent (OrganiseEventType type) {
        return _mostRecent.get (type);
    }

    @Override
    public void keyTyped (KeyEvent occur) {
        event (keyTyped).event (occur).fire ();
    }

    @Override
    public void keyPressed (KeyEvent occur) {
        event (keyPressed).event (occur).fire ();
    }

    @Override
    public void keyReleased (KeyEvent occur) {
        event (keyReleased).event (occur).fire ();
    }
}
