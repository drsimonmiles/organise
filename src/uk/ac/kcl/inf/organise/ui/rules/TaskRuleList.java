package uk.ac.kcl.inf.organise.ui.rules;

import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;
import uk.ac.kcl.inf.organise.rules.Rule;

public class TaskRuleList extends ItemListPanel <Rule> implements OrganiseEventListener {
    private Task _task;
    private final Database _database;
    
    public TaskRuleList (Database database) {
        super (true, true);
        _task = null;
        _database = database;
    }
    
    public void open (Task task) {
        _task = task;
        clear ();
        for (Rule rule : _database.getTaskRules (task)) {
            addItem (rule);
        }
    }            

    @Override
    public void organiseEvent (OrganiseEvent event) {
        switch (event._type) {
            case ruleAdded:
                if (event._rule.getOwner () == _task) {
                    addItem (event._rule);
                }
                break;
            case ruleRemoved:
                if (event._rule.getOwner () == _task) {
                    removeItem (event._rule);
                }
                break;
        }
    }
}
