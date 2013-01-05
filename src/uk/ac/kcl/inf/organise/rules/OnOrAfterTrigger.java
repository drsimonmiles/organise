package uk.ac.kcl.inf.organise.rules;

import java.util.Calendar;
import java.util.Date;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;

public class OnOrAfterTrigger extends Trigger implements OrganiseEventListener {
    private Date _date;

    public OnOrAfterTrigger (Date date, EventBus bus) {
        _date = date;
        bus._listeners.add (this);
    }

    @Override
    public void organiseEvent (OrganiseEvent event) {
        Date today;

        switch (event._type) {
            case opening:
                today = new Date ();
                if (sameDate (today, _date) || today.after (_date)) {
                    triggered ();
                }
                break;
        }
    }
    
    public void postpone (int days) {
        Calendar cal1 = Calendar.getInstance ();
        
        cal1.setTime (_date);
        cal1.add (Calendar.DAY_OF_MONTH, days);
        
        _date = cal1.getTime ();
    }

    private static boolean sameDate (Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance ();
        Calendar cal2 = Calendar.getInstance ();

        cal1.setTime (date1);
        cal2.setTime (date2);

        return cal1.get (Calendar.YEAR) == cal2.get (Calendar.YEAR)
               && cal1.get (Calendar.DAY_OF_YEAR) == cal2.get (Calendar.DAY_OF_YEAR);
    }
}
