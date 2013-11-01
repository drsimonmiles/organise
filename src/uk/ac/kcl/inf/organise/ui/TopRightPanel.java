package uk.ac.kcl.inf.organise.ui;

//import com.toedter.calendar.JCalendar;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.data.TaskUtils;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEventType;

public class TopRightPanel extends JPanel implements PropertyChangeListener {
    private final JTabbedPane _main;
    private final PreviousTaskPanel _previous;
    private final Database _database;
    private final EventBus _bus;
//    private final JCalendar _calendar;
    private final ProjectTasks _events;
    
    public TopRightPanel (Database database, EventBus bus) {
//        _calendar = new JCalendar ();
        _events = new ProjectTasks (Database.EVENTS_PROJECT, database, bus);
        
        JScrollPane notes = new JScrollPane (new NotesPanel (bus), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //JScrollPane triggers = new JScrollPane (new TriggersPanel (bus, database), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //JScrollPane calScroll = new JScrollPane (new JLabel ("Obsolete"), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JScrollPane evtScroll = new JScrollPane (_events, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        _database = database;
        _bus = bus;
        
        _main = new JTabbedPane ();
        _main.insertTab (Database.EVENTS_PROJECT, null, evtScroll, "Events", 0);
        _main.insertTab ("Notes", null, notes, "Task notes", 1);
        //_main.insertTab ("Triggers", null, triggers, "Triggers", 2);
        //_main.insertTab ("Calendar", null, calScroll, "Calendar", 3);
        _main.setSelectedIndex (0);

//        _calendar.getDayChooser ().addPropertyChangeListener ("day", this);
        _previous = new PreviousTaskPanel (bus);

        setLayout (new BorderLayout ());
        add (_main, BorderLayout.CENTER);
        add (_previous, BorderLayout.SOUTH);
    }

    @Override
    public void propertyChange (PropertyChangeEvent event) {
        Date date;
        Task task;
        
        if (event.getPropertyName ().equals ("day")) {
//s            date = (Date) _calendar.getDate ();
//            task = TaskUtils.getDateTask (date, _database, _bus);
//            _bus.event (OrganiseEventType.anchorChanged).task (task).fire ();
            
        }
    }
}
