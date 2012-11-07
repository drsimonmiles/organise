package uk.ac.kcl.inf.organise.ui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JTextField;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;
import uk.ac.kcl.inf.settings.Settings;

public class PathSettingField extends JTextField implements FocusListener, OrganiseEventListener {
    private final Object _setting;

    public PathSettingField (Object setting, EventBus bus) {
        Path initial = Settings.get ().getFilePath (setting);

        _setting = setting;
        if (initial == null) {
            setText ("");
        } else {
            setText (initial.toString ());
        }
        bus._listeners.add (this);
        addFocusListener (this);
    }

    @Override
    public void focusGained (FocusEvent e) {
    }

    @Override
    public void focusLost (FocusEvent e) {
        save ();
    }

    @Override
    public void organiseEvent (OrganiseEvent event) {
        switch (event._type) {
            case closing:
                save ();
                break;
        }
    }

    public void save () {
        try {
            Settings.get ().saveFilePath (_setting, Paths.get (getText ()));
        } catch (Throwable cannotSave) {
            cannotSave.printStackTrace ();
        }
    }
}
