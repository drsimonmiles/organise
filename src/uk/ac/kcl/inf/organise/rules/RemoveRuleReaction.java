package uk.ac.kcl.inf.organise.rules;

import uk.ac.kcl.inf.organise.data.Database;

public class RemoveRuleReaction implements Reaction {
    private final Database _database;

    public RemoveRuleReaction (Database database) {
        _database = database;
    }
    
    @Override
    public void perform (Rule rule) {
        _database.deleteRule (rule);
    }

    @Override
    public String toString () {
        return "Remove rule";
    }
}
