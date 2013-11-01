package uk.ac.kcl.inf.organise.ui;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import uk.ac.kcl.inf.organise.SettingType;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;
import uk.ac.kcl.inf.settings.Settings;

public class TopPanel extends JSplitPane implements OrganiseEventListener {
    public TopPanel (Database database, EventBus bus) {
        super (HORIZONTAL_SPLIT, true,
               new MultiImmediates (database, bus),
               new JScrollPane (new NotesPanel (bus), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
               //new TopRightPanel (database, bus));
        Settings.get ().loadPosition (SettingType.topPaneSplit, this);
        bus._listeners.add (this);
    }

    @Override
    public void organiseEvent (OrganiseEvent event) {
        switch (event._type) {
            case closing:
                Settings.get ().savePosition (SettingType.topPaneSplit, this);
                break;
        }
    }
}
