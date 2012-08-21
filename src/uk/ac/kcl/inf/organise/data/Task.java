package uk.ac.kcl.inf.organise.data;

import java.util.LinkedList;
import java.util.List;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEventType;

public class Task {
    private String _project;
    private String _text;
    private Priority _priority;
    private List<Trigger> _triggers;
    private String _notes;
    private EventBus _bus;
    private boolean _isTriggerOnly;

    public Task (String project, String text, Priority priority, String notes, boolean isTriggerOnly, EventBus bus, Database database) {
        _project = project;
        _text = text;
        _priority = priority;
        _triggers = new LinkedList<> ();
        _notes = notes;
        _bus = bus;
        if (text == null) {
            throw new RuntimeException ();
        }
        _isTriggerOnly = isTriggerOnly;
    }

    public void addTrigger (Trigger trigger) {
        _triggers.add (trigger);
        _bus.event (OrganiseEventType.triggerAdded).task (this).trigger (trigger).fire ();
    }

    public String getNotes () {
        return _notes;
    }

    public Priority getPriority () {
        return _priority;
    }

    public String getProject () {
        return _project;
    }

    public String getText () {
        return _text;
    }

    public List<Trigger> getTriggers () {
        return _triggers;
    }

    public void trigger (Task triggering) {
        for (Trigger trigger : _triggers) {
            trigger.completed (triggering);
        }
    }

    public boolean isTriggerOnly () {
        return _isTriggerOnly;
    }

    public void removeTrigger (Trigger trigger) {
        if (_triggers.contains (trigger)) {
            _triggers.remove (trigger);
            _bus.event (OrganiseEventType.triggerDeleted).task (this).trigger (trigger).fire ();
        }
    }

    public void setPriority (Priority priority) {
        Priority prior = _priority;
        _priority = priority;
        _bus.event (OrganiseEventType.taskPriorityChanged).task (this).prior (prior).fire ();
    }

    public void setProject (String newName) {
        _project = newName;
    }

    public void setNotes (String notes) {
        String prior = _notes;
        _notes = notes;
        _bus.event (OrganiseEventType.taskNotesChanged).task (this).prior (prior).fire ();
    }

    public void setText (String text) {
        String prior = _text;
        _text = text;
        _bus.event (OrganiseEventType.taskTextChanged).task (this).prior (prior).fire ();
    }

    public void setTriggerOnly (boolean isTriggerOnly) {
        _isTriggerOnly = isTriggerOnly;
    }

    @Override
    public String toString () {
        return _text;
    }
}
