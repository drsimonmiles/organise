package uk.ac.kcl.inf.organise.ui;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import uk.ac.kcl.inf.organise.SettingType;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Priority;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;
import uk.ac.kcl.inf.settings.Settings;

public class MultiImmediates extends JSplitPane implements OrganiseEventListener {
    public MultiImmediates (Database database, EventBus bus) {
        super (VERTICAL_SPLIT, true,
               new JScrollPane (new ImmediateTasks (Priority.immediate, database, bus), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
               new JScrollPane (new ImmediateTasks (Priority.urgent, database, bus), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        Settings.get ().loadPosition (SettingType.priorityPaneSplit, this);
        bus._listeners.add (this);
    }

    @Override
    public void organiseEvent (OrganiseEvent event) {
        switch (event._type) {
            case closing:
                Settings.get ().savePosition (SettingType.priorityPaneSplit, this);
                break;
            case taskAdded:
                validate ();
                repaint ();
                break;
        }
    }
}
