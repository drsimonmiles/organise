package uk.ac.kcl.inf.organise.data;

import java.util.LinkedList;
import java.util.List;

public class Trigger {
    private List<Task> _triggering;
    private ActionType _type;
    private Task _affected;
    private Database _database;

    public Trigger (Task triggering, Task affected, ActionType action, Database database) {
        if (triggering == null) {
            throw new RuntimeException ("Given null task to trigger");
        }
        _triggering = new LinkedList<> ();
        _triggering.add (triggering);
        _type = action;
        _affected = affected;
        _database = database;
    }

    public Trigger (Task triggering, Task affected, Database database) {
        this (triggering, affected, ActionType.immediateIfAny, database);
    }

    public ActionType getActionType () {
        return _type;
    }

    public List<Task> getTriggeringTasks () {
        return _triggering;
    }

    public void completed (Task trigger) {
        if (!_triggering.contains (trigger)) {
            return;
        }
        switch (_type) {
            case immediateIfAll:
            case urgentIfAll:
            case completeIfAll:
                _triggering.remove (trigger);
                if (!_triggering.isEmpty ()) {
                    return;
                }
                break;
        }
        switch (_type) {
            case immediateIfAny:
            case immediateIfAll:
                _affected.setPriority (Priority.immediate);
                break;
            case urgentIfAny:
            case urgentIfAll:
                _affected.setPriority (Priority.urgent);
                break;
            case completeIfAny:
            case completeIfAll:
                _database.completeTask (_affected);
                break;
        }
        _affected.removeTrigger (this);
    }

    @Override
    public String toString () {
        switch (_type) {
            case immediateIfAny:
                return "Immediate if ANY of";
            case immediateIfAll:
                return "Immediate if ALL of";
            case urgentIfAny:
                return "Urgent if ANY of";
            case urgentIfAll:
                return "Urgent if ALL of";
            case completeIfAny:
                return "Complete if ANY of";
            case completeIfAll:
                return "Complete if ALL of";
        }
        return _type + " " + _triggering;
    }
}
