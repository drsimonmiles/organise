package uk.ac.kcl.inf.organise.rules;

import uk.ac.kcl.inf.organise.data.Database;

public class RemoveRuleReaction implements Reaction {
    private final Database _database;
    private final Rule _rule;

    public RemoveRuleReaction (Rule rule, Database database) {
        _rule = rule;
        _database = database;
    }
    
    @Override
    public void perform () {
        _database.deleteRule (_rule);
    }

    @Override
    public String toString () {
        return "Remove rule";
    }
}
