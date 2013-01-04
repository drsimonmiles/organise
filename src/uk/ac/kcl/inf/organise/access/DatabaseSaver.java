package uk.ac.kcl.inf.organise.access;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import uk.ac.kcl.inf.organise.SettingType;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Priority;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.data.TaskUtils;
import uk.ac.kcl.inf.organise.data.Trigger;
import uk.ac.kcl.inf.settings.Settings;

public class DatabaseSaver {
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
        out.println ("<database version=\"0.3\">");
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

    private String serialise (Priority priority) {
        switch (priority) {
            case normal:
                return DatabaseLoader.NORMAL_PRIORITY;
            case urgent:
                return DatabaseLoader.URGENT_PRIORITY;
            case immediate:
                return DatabaseLoader.IMMEDIATE_PRIORITY;
        }

        System.err.println ("Do not know how to serialise priority: " + priority);
        return DatabaseLoader.NORMAL_PRIORITY;
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
