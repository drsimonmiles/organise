package uk.ac.kcl.inf.organise.ui;

import java.awt.Component;
import java.util.Comparator;
import javax.swing.JScrollPane;
import uk.ac.kcl.inf.organise.data.Database;

public class SortPanelByProjectID implements Comparator<Component> {
    private static SortPanelByProjectID _default = null;
    
    private SortPanelByProjectID () {
    }
    
    @Override
    public int compare (Component panel1, Component panel2) {
        String project1 = extractProject (panel1);
        String project2 = extractProject (panel2);
        
        if (project1.equals ("+")) {
            project1 = "zzzzzzzzzzz";
        }
        if (project2.equals ("+")) {
            project2 = "zzzzzzzzzzz";
        }
        return project1.compareTo (project2);
    }

    private static String extractProject (Component component) {
        if (component instanceof JScrollPane) {
            return extractProject (((JScrollPane) component).getViewport ().getView ());
        }
        if (component instanceof ProjectTasks) {
            return ((ProjectTasks) component).getProject ();
        }
        return "";
    }
    
    public static SortPanelByProjectID get () {
        if (_default == null) {
            _default = new SortPanelByProjectID ();
        }
        return _default;
    }
}
