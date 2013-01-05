package uk.ac.kcl.inf.organise.rules;

import uk.ac.kcl.inf.organise.data.Task;

public class AllocateReaction implements Reaction {
    private final Task _task;
    private final int _minutes;

    public AllocateReaction (Task task, int minutes) {
        _task = task;
        _minutes = minutes;
    }
    
    @Override
    public void perform () {
        _task.setAllocated (_task.getAllocated () + _minutes);
    }

}
