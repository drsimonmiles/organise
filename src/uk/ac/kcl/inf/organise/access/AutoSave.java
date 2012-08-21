package uk.ac.kcl.inf.organise.access;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import uk.ac.kcl.inf.organise.Controller;

public class AutoSave extends TimerTask {

    private final Controller _projects;

    public AutoSave (Controller projects) {
        _projects = projects;
    }

    public void run () {
        try {
            _projects.save ();
        } catch (IOException ex) {
            ex.printStackTrace ();
        }
    }
    
    public static void schedule (Controller projects) {
        new Timer ().scheduleAtFixedRate (new AutoSave (projects), 1800000, 1800000); // 5 minutes
    }
}
