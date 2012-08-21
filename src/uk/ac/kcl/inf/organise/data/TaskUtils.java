package uk.ac.kcl.inf.organise.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import uk.ac.kcl.inf.organise.events.EventBus;

public class TaskUtils {
    public static String DATES_PROJECT = "--Calendar--";
    private static final SimpleDateFormat _format = new SimpleDateFormat ("yyyy MMM dd");

    public static Task addDateTask (Date date, Database database, EventBus bus) {
        Task task = new Task (DATES_PROJECT, _format.format (date), Priority.normal, "", true, bus, database);

        database.addTask (task);

        return task;
    }

    public static void cleanTasks (Database database) {
        List remove = new LinkedList<> ();
        
        for (Task task : database.getTasks ()) {
            if (task.isTriggerOnly () && !database.isTrigger (task) &&
                !(task.getProject ().equals (Database.EVENTS_PROJECT) && task.getText ().trim ().equals (""))) {
                remove.add (task);
            }
        }
        database.deleteTasks (remove);
    }

    public static Task getDateTask (Date date, Database database, EventBus bus) {
        String text = _format.format (date);

        for (Task task : database.getTasks ()) {
            if (task.getProject ().equals (DATES_PROJECT) && task.getText ().equals (text)) {
                return task;
            }
        }

        return addDateTask (date, database, bus);
    }

    public static boolean sameDate (Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance ();
        Calendar cal2 = Calendar.getInstance ();

        cal1.setTime (date1);
        cal2.setTime (date2);

        return cal1.get (Calendar.YEAR) == cal2.get (Calendar.YEAR)
               && cal1.get (Calendar.DAY_OF_YEAR) == cal2.get (Calendar.DAY_OF_YEAR);
    }

    public static void triggerDates (Database database) {
        try {
            Date today = new Date ();
            Date date;

            for (Task task : new LinkedList<> (database.getTasks ())) {
                if (task.getProject ().equals (TaskUtils.DATES_PROJECT)) {
                    date = _format.parse (task.getText ());
                    if (sameDate (today, date) || today.after (date)) {
                        database.completeTask (task);
                    }
                }
            }
        } catch (ParseException ex) {
            ex.printStackTrace ();
        }
    }
}
