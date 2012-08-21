package uk.ac.kcl.inf.organise.ui;

import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.data.Trigger;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;

public class TriggerListModel extends AbstractListModel implements OrganiseEventListener {
    private Task _task;

    public TriggerListModel (EventBus bus) {
        _task = null;
        bus._listeners.add (this);
    }

    protected List<Trigger> getTriggers () {
        if (_task != null) {
            return _task.getTriggers ();
        } else {
            return Collections.<Trigger> emptyList ();
        }
    }

    public void open (Task task) {
        _task = task;
        fireContentsChanged (this, 0, getTriggers ().size () - 1);
    }

    @Override
    public void organiseEvent (OrganiseEvent event) {
        switch (event._type) {
            case taskInFocus:
                open (event._task);
                break;
            case triggerAdded:
                if (_task != null && _task.equals (event._task)) {
                    open (_task);
                }
                break;
            case triggerDeleted:
                if (_task != null && _task.equals (event._task)) {
                    open (_task);
                }
                break;
        }
    }

    @Override
    public int getSize () {
        return getTriggers ().size ();
    }

    @Override
    public Object getElementAt (int index) {
        if (getTriggers ().isEmpty ()) {
            return null;
        } else {
            return getTriggers ().get (index);
        }
    }
}
