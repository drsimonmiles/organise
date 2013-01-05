package uk.ac.kcl.inf.organise.rules;

import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;

public class CompletionTrigger extends Trigger implements OrganiseEventListener {
    private final Task _task;
    
    public CompletionTrigger (Task task, EventBus bus) {
        _task = task;
        bus._listeners.add (this);
    }

    public Task getTask () {
        return _task;
    }
    
    @Override
    public void organiseEvent (OrganiseEvent event) {
        switch (event._type) {
            case taskCompleted:
                if (event._task == _task) {
                    triggered ();
                }
                break;
        }
    }
}
