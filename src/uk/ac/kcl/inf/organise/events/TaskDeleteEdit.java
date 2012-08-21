package uk.ac.kcl.inf.organise.events;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Task;

public class TaskDeleteEdit extends AbstractUndoableEdit {
    private final Database _database;
    private final Task _task;
    private final Task _followed;
    
    public TaskDeleteEdit (Database database, Task task, Task followed) {
        _database = database;
        _task = task;
        _followed = followed;
    }
    
    @Override
    public void undo () throws CannotUndoException {
        super.undo ();
        _database.addTask (_task, _followed);
    }
}
