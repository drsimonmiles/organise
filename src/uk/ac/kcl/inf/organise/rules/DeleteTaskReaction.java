package uk.ac.kcl.inf.organise.rules;

import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Task;

public class DeleteTaskReaction implements Reaction {
    private final Task _task;
    private final Database _database;

    public DeleteTaskReaction (Task task, Database database) {
        _task = task;
        _database = database;
    }
    
    @Override
    public void perform () {
        _database.deleteTask (_task);
    }

}
