package uk.ac.kcl.inf.organise;

import java.io.IOException;
import uk.ac.kcl.inf.organise.access.AccessException;
import uk.ac.kcl.inf.organise.access.SummaryGenerator;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.ui.MainWindow;
import uk.ac.kcl.inf.settings.OpenInstanceRenewer;
import uk.ac.kcl.inf.settings.Settings;

public class Organise {
    private final Controller _controller;

    public Organise () throws AccessException {
        EventBus bus = new EventBus ();
        Database database = new Database (bus);
        MainWindow display = new MainWindow (database, bus);

        _controller = new Controller (database, display, bus);
    }

    public Database getDatabase () {
        return _controller.getDatabase ();
    }

    public Organise run () throws IOException {
        _controller.run ();
        return this;
    }

    public static void main (String[] arguments) {
        Organise organiser;
        boolean summary = false;
        boolean force = false;

        Settings.initialise (Organise.class);

        if (arguments.length > 0 && arguments[0].equals ("summary")) {
            summary = true;
        }
        if (arguments.length > 0 && arguments[0].equals ("force")) {
            force = true;
        }
        if (!summary && !force) {
            if (Settings.get ().isOpenInstance ()) {
                System.err.println ("Already open instance");
                return;
            } else {
                OpenInstanceRenewer.get ().start ();
            }
        }
        try {
            organiser = new Organise ();
            if (summary) {
                SummaryGenerator.generate (arguments[1], organiser.getDatabase ());
                System.exit (0);
            } else {
                organiser.run ();
            }
        } catch (Throwable problem) {
            problem.printStackTrace ();
            System.exit (1);
        }
    }
}
