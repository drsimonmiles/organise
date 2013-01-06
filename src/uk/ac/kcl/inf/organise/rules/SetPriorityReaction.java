package uk.ac.kcl.inf.organise.rules;

import uk.ac.kcl.inf.organise.data.Priority;
import uk.ac.kcl.inf.organise.data.Task;

public class SetPriorityReaction implements Reaction {
    private Task _task;
    private Priority _priority;

    public SetPriorityReaction (Task task, Priority priority) {
        _task = task;
        _priority = priority;
    }
    
    public Priority getPriority () {
        return _priority;
    }
    
    @Override
    public void perform () {
        _task.setPriority (_priority);
    }

    @Override
    public String toString () {
        return "Set priority to " + _priority;
    }
}
