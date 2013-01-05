package uk.ac.kcl.inf.organise.access;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.List;
import uk.ac.kcl.inf.organise.SettingType;
import static uk.ac.kcl.inf.organise.access.DatabaseLoader.*;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Priority;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.data.TaskUtils;
import uk.ac.kcl.inf.organise.data.Trigger;
import uk.ac.kcl.inf.organise.rules.AllocateReaction;
import uk.ac.kcl.inf.organise.rules.CompletionTrigger;
import uk.ac.kcl.inf.organise.rules.DeleteTaskReaction;
import uk.ac.kcl.inf.organise.rules.OnOrAfterTrigger;
import uk.ac.kcl.inf.organise.rules.PostponeRuleReaction;
import uk.ac.kcl.inf.organise.rules.Reaction;
import uk.ac.kcl.inf.organise.rules.RemoveRuleReaction;
import uk.ac.kcl.inf.organise.rules.Rule;
import uk.ac.kcl.inf.organise.rules.SetPriorityReaction;
import uk.ac.kcl.inf.settings.Settings;

public class DatabaseSaver {
    private int index (Task task, Database database) {
        return database.getTasks ().indexOf (task);
    }
    
    public void save (Database database) throws IOException {
        Path source = Settings.get ().getFilePath (SettingType.databaseDirectory);
        save (database, source.resolve (DatabaseLoader.DATABASE_FILE));        
    }
    
    public void save (Database database, Path target) throws IOException {
        try (PrintWriter out = new PrintWriter (Files.newBufferedWriter (target, StandardCharsets.UTF_8))) {
            save (database, out);
        }
    }

    public void save (Database database, PrintWriter out) {
        out.println ("<database version=\"0.4\">");
        TaskUtils.cleanTasks (database);
        for (Task task : database.getTasks ()) {
            save (database, task, out);
        }
        out.println ("</database>");
    }

    private void save (Database database, Task task, PrintWriter out) {
        out.println (" <task>");
        out.println ("  <text>" + xmlText (task.getText ()) + "</text>");
        out.println ("  <project>" + xmlText (task.getProject ()) + "</project>");
        out.println ("  <priority>" + serialise (task.getPriority ()) + "</priority>");
        for (Rule rule : database.getTaskRules (task)) {
            save (database, task, rule, out);
        }
        for (Trigger trigger : task.getTriggers ()) {
            for (Task triggering : trigger.getTriggeringTasks ()) {
                out.println ("  <trigger>" + database.getTasks ().indexOf (triggering) + "</trigger>");
            }
        }
        out.println ("  <notes>" + xmlText (task.getNotes ()) + "</notes>");
        out.println ("  <allocated>" + task.getAllocated () + "</allocated>");
        out.println ("  <triggerOnly>" + task.isTriggerOnly () + "</triggerOnly>");
        out.println (" </task>");
    }

    private void save (Database database, Task task, Rule rule, PrintWriter out) {
        out.println ("  <rule>");
        out.println ("   <requiresAll>" + rule.doesRequireAll () + "</requiresAll>");
        for (uk.ac.kcl.inf.organise.rules.Trigger trigger : rule.getTriggers ()) {
            save (database, task, rule, trigger, out);
        }
        for (Reaction reaction : rule.getReactions ()) {
            save (database, task, rule, reaction, out);
        }
        out.println ("  </rule>");
    }
    
    private void save (Database database, Task task, Rule rule, uk.ac.kcl.inf.organise.rules.Trigger trigger, PrintWriter out) {
        Task depends;
        
        out.println ("   <trigger>");
        if (trigger instanceof CompletionTrigger) {
            out.println ("    <type>" + COMPLETION_TRIGGER_TYPE + "</type>");
            depends = ((CompletionTrigger) trigger).getTask ();
            if (depends != task) {
                out.println ("    <task>" + index (depends, database) + "</task>");
            }
        }
        if (trigger instanceof OnOrAfterTrigger) {
            out.println ("    <type>" + ONORAFTER_TRIGGER_TYPE + "</type>");
            out.println ("    <date>" + serialise (((OnOrAfterTrigger) trigger).getDate ()) + "</date>");
        }
        out.println ("   </trigger>");
    }
    
    private void save (Database database, Task task, Rule rule, Reaction reaction, PrintWriter out) {
        out.println ("   <reaction>");
        if (reaction instanceof AllocateReaction) {
            out.println ("    <type>" + ALLOCATE_REACTION_TYPE + "</type>");
            out.println ("    <minutes>" + ((AllocateReaction) reaction).getMinutes () + "</minutes>");
        }
        if (reaction instanceof DeleteTaskReaction) {
            out.println ("    <type>" + DELETE_TASK_REACTION_TYPE + "</type>");
        }
        if (reaction instanceof PostponeRuleReaction) {
            out.println ("    <type>" + POSTPONE_RULE_REACTION_TYPE + "</type>");
            out.println ("    <days>" + ((PostponeRuleReaction) reaction).getDays () + "</days>");
        }
        if (reaction instanceof RemoveRuleReaction) {
            out.println ("    <type>" + REMOVE_RULE_REACTION_TYPE + "</type>");
        }
        if (reaction instanceof SetPriorityReaction) {
            out.println ("    <type>" + SET_PRIORITY_REACTION_TYPE + "</type>");
            out.println ("    <priority>" + serialise (((SetPriorityReaction) reaction).getPriority ()) + "</priority>");
        }
        out.println ("   </reaction>");
    }
    
    private String serialise (Priority priority) {
        switch (priority) {
            case normal:
                return NORMAL_PRIORITY;
            case urgent:
                return URGENT_PRIORITY;
            case immediate:
                return IMMEDIATE_PRIORITY;
        }

        System.err.println ("Do not know how to serialise priority: " + priority);
        return DatabaseLoader.NORMAL_PRIORITY;
    }
    
    private String serialise (Date date) {
        return DATE_FORMAT.format (date);
    }

    public void saveHistory (List<Task> history) throws IOException {
        Path directory = Settings.get ().getFilePath (SettingType.databaseDirectory);
        Path target = directory.resolve (DatabaseLoader.HISTORY_FILE);
        StandardOpenOption open = StandardOpenOption.APPEND;
        
        if (!Files.exists (target)) {
            open = StandardOpenOption.CREATE;
        }
        try (PrintWriter out = new PrintWriter (Files.newBufferedWriter (target, StandardCharsets.UTF_8, open))) {
            for (Task task : history) {
                out.println ("[" + task.getProject () + "] " + task.getText ());
            }
        }
    }    
    
    private String xmlText (String original) {
        return removeLT (removeAmp (original, 0), 0);
    }

    private String removeAmp (String original, int from) {
        int amp = original.indexOf ('&', from);

        if (amp >= 0) {
            return removeAmp (original.substring (0, amp) + "&amp;" + original.substring (amp + 1), amp + 5);
        }

        return original;
    }

    private String removeLT (String original, int from) {
        int lt = original.indexOf ('<', from);

        if (lt >= 0) {
            return removeLT (original.substring (0, lt) + "&lt;" + original.substring (lt + 1), lt + 4);
        }

        return original;
    }
}
