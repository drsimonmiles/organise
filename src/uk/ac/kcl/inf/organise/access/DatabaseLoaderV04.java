package uk.ac.kcl.inf.organise.access;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;
import static uk.ac.kcl.inf.organise.access.DatabaseLoader.*;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Priority;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.data.Trigger;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.rules.AllocateReaction;
import uk.ac.kcl.inf.organise.rules.CompletionTrigger;
import uk.ac.kcl.inf.organise.rules.DeleteTaskReaction;
import uk.ac.kcl.inf.organise.rules.OnOrAfterTrigger;
import uk.ac.kcl.inf.organise.rules.PostponeRuleReaction;
import uk.ac.kcl.inf.organise.rules.RemoveRuleReaction;
import uk.ac.kcl.inf.organise.rules.Rule;
import uk.ac.kcl.inf.organise.rules.SetPriorityReaction;

public class DatabaseLoaderV04 {
    private final EventBus _bus;

    public DatabaseLoaderV04 (EventBus bus) {
        _bus = bus;
    }

    public void loadDatabase (Database database, Document document) throws AccessException {
        List<Integer> triggers = new LinkedList<> ();
        
        loadTasks (database, document.query ("/database/task"), triggers);
        loadTriggers (database, triggers);
    }
    
    private void loadRules (int taskIndex, Database database, Node taskElement) {
        Nodes rules = taskElement.query ("rule");
        Task task = database.getTasks ().get (taskIndex);
        
        for (int index = 0; index < rules.size (); index += 1) {
            loadRule (task, database, rules.get (index));
        }
    }
    
    private void loadRule (Task task, Database database, Node element) {
        boolean requiresAll = DatabaseLoader.getBoolean (element, "requiresAll/text()");
        Rule rule = new Rule (task, requiresAll);
        Nodes triggers = element.query ("trigger");
        Nodes reactions = element.query ("reaction");
        
        for (int index = 0; index < triggers.size (); index += 1) {
            loadRuleTrigger (rule, database, triggers.get (index));
        }
        for (int index = 0; index < reactions.size (); index += 1) {
            loadRuleReaction (rule, database, reactions.get (index));
        }
        
        database.addRule (rule);
    }
    
    private void loadRuleReaction (Rule rule, Database database, Node element) {
        String type = DatabaseLoader.getText (element, "type", false);
        int minutes, days;
        Priority priority;
        
        switch (type) {
            case ALLOCATE_REACTION_TYPE:
                minutes = DatabaseLoader.getInteger (element, "minutes/text()");
                rule.addReaction (new AllocateReaction (rule.getOwner (), minutes));
                break;
            case DELETE_TASK_REACTION_TYPE:
                rule.addReaction (new DeleteTaskReaction (rule.getOwner (), database));
                break;
            case POSTPONE_RULE_REACTION_TYPE:
                days = DatabaseLoader.getInteger (element, "days/text()");
                rule.addReaction (new PostponeRuleReaction (days));
                break;
            case REMOVE_RULE_REACTION_TYPE:
                rule.addReaction (new RemoveRuleReaction (database));               
                break;
            case SET_PRIORITY_REACTION_TYPE:
                priority = DatabaseLoader.getPriority (element, "priority/text()");
                rule.addReaction (new SetPriorityReaction (rule.getOwner (), priority));
                break;
        }
    }

    private void loadRuleTrigger (Rule rule, Database database, Node element) {
        String type = DatabaseLoader.getText (element, "type", false);
        int dependent;
        Date date;
        
        switch (type) {
            case COMPLETION_TRIGGER_TYPE:
                dependent = DatabaseLoader.getInteger (element, "task/text()");
                if (dependent >= 0) {
                    rule.addTrigger (new CompletionTrigger (database.getTasks ().get (dependent), _bus));
                } else {
                    rule.addTrigger (new CompletionTrigger (rule.getOwner (), _bus));
                }                
                break;
            case ONORAFTER_TRIGGER_TYPE:
                date = DatabaseLoader.getDate (element, "date/text()");
                rule.addTrigger (new OnOrAfterTrigger (date, _bus));
                break;
        }
    }

    private void loadTasks (Database database, Nodes elements, List<Integer> triggers) {
        for (int index = 0; index < elements.size (); index += 1) {
            loadTask (database, elements.get (index), triggers);
        }
        for (int index = 0; index < elements.size (); index += 1) {
            loadRules (index, database, elements.get (index));
        }
    }
        
    private void loadTask (Database database, Node element, List<Integer> triggers) {
        String text = DatabaseLoader.getText (element, "text/text()", false);
        String project = DatabaseLoader.getText (element, "project/text()", false);
        Priority priority = DatabaseLoader.getPriority (element, "priority/text()");
        int allocated = DatabaseLoader.getInteger (element, "allocated/text()");
        int triggerID = DatabaseLoader.getInteger (element, "trigger/text()");
        String notes = DatabaseLoader.getText (element, "notes/text()", false);
        boolean triggerOnly = DatabaseLoader.getBoolean (element, "triggerOnly/text()");
        Task task = new Task (project, text, priority, notes, allocated, triggerOnly, _bus, database);

        database.addTask (task);
        triggers.add (triggerID);
    }
    
    private void loadTriggers (Database database, List<Integer> triggers) {
        List<Task> tasks = database.getTasks ();
        Task task;
        int trigger;
        
        for (int index = 0; index < triggers.size (); index += 1) {
            task = tasks.get (index);
            trigger = triggers.get (index);
            if (trigger >= 0 && tasks.size () > trigger) {
                task.addTrigger (new Trigger (tasks.get (trigger), task, database));
            }
        }
    }
}
