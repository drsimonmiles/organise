package uk.ac.kcl.inf.organise.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEvent;
import uk.ac.kcl.inf.organise.events.OrganiseEventListener;
import uk.ac.kcl.inf.organise.events.OrganiseEventType;

public class ProjectTab extends JLabel implements ActionListener, MouseListener, OrganiseEventListener {
    private final ProjectsPanel _parent;
    private String _project;
    private final JPopupMenu _context;
    private final Component _tabbed;
    private final Database _database;

    public ProjectTab (ProjectsPanel parent, String project, Component tabbed, Database database, EventBus bus) {
        super (project);

        JMenuItem item;

        _parent = parent;
        _project = project;
        _tabbed = tabbed;
        _database = database;
        addMouseListener (this);
        bus._listeners.add (this);

        _context = new JPopupMenu ();
        item = new JMenuItem ("Rename");
        item.addActionListener (this);
        _context.add (item);
        item = new JMenuItem ("Delete");
        item.addActionListener (this);
        _context.add (item);
    }

    @Override
    public void actionPerformed (ActionEvent event) {
        if (event.getActionCommand ().equals ("Rename")) {
            rename ();
        }
        if (event.getActionCommand ().equals ("Delete")) {
            _database.deleteTasks (_database.getProjectTasks (_project));
        }
    }
    
    public static Color calculateColour (String name) {
        if (name == null) {
            return Color.WHITE;
        }
        
        int red = calculateShade (name.charAt (0));
        int green = calculateShade (name.charAt (1));
        int blue = calculateShade (name.charAt (2));
        
        return new Color (red, green, blue);
    }
    
    private static int calculateShade (char character) {
        int shade32 = 27;
        
        if (Character.isLetter (character)) {
            shade32 = ((int) Character.toLowerCase (character)) - ((int) 'a');
        }
        if (Character.isDigit (character)) {
            shade32 = 26;
        }
        
        return shade32 * 2 + 192;
    }

    private void maybeShowPopup (MouseEvent e) {
        _parent.setSelectedComponent (_tabbed);
        if (e.isPopupTrigger ()) {
            _context.show (e.getComponent (), e.getX (), e.getY ());
        }
    }

    @Override
    public void mouseClicked (MouseEvent e) {
    }

    @Override
    public void mousePressed (MouseEvent e) {
        maybeShowPopup (e);
    }

    @Override
    public void mouseReleased (MouseEvent e) {
        maybeShowPopup (e);
    }

    @Override
    public void mouseEntered (MouseEvent e) {
    }

    @Override
    public void mouseExited (MouseEvent e) {
    }

    private void rename () {
        String name = JOptionPane.showInputDialog (this, "Project name", "Enter new project name", JOptionPane.QUESTION_MESSAGE);

        _database.renameProject (_project, name);
        _project = name;
    }
    
    @Override
    public void organiseEvent (OrganiseEvent event) {
        if (event._type == OrganiseEventType.projectRenamed && event._priorString.equals (_project)) {
            _project = event._project;
            setText (event._project);
            repaint ();
        }
    }
}
