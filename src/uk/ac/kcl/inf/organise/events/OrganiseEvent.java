package uk.ac.kcl.inf.organise.events;

import java.awt.event.KeyEvent;
import uk.ac.kcl.inf.organise.data.Priority;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.data.Trigger;

public class OrganiseEvent {
    private EventBus _bus;
    public final OrganiseEventType _type;
    public String _project;
    public Task _task, _priorTask, _child;
    public Trigger _trigger;
    public KeyEvent _keyEvent;
    public String _priorString;
    public Priority _priorPriority;
    public Trigger _priorTrigger;
    public int _position, _priorInteger;

    public OrganiseEvent (OrganiseEventType type, EventBus bus) {
        _bus = bus;
        _type = type;
    }

    public OrganiseEvent child (Task child) {
        _child = child;
        return this;
    }
    
    public OrganiseEvent event (KeyEvent event) {
        _keyEvent = event;
        return this;
    }

    public void fire () {
        _bus.fire (this);
    }

    public OrganiseEvent prior (String priorString) {
        _priorString = priorString;
        return this;
    }

    public OrganiseEvent prior (Priority priorPriority) {
        _priorPriority = priorPriority;
        return this;
    }

    public OrganiseEvent prior (Task priorTask) {
        _priorTask = priorTask;
        return this;
    }

    public OrganiseEvent prior (Trigger priorTrigger) {
        _priorTrigger = priorTrigger;
        return this;
    }
    
    public OrganiseEvent prior (int priorInteger) {
        _priorInteger = priorInteger;
        return this;
    }

    public OrganiseEvent project (String project) {
        _project = project;
        return this;
    }

    public OrganiseEvent task (Task task) {
        _task = task;
        return this;
    }

    public OrganiseEvent trigger (Trigger trigger) {
        _trigger = trigger;
        return this;
    }
}
