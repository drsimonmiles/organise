package uk.ac.kcl.inf.organise.access;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import uk.ac.kcl.inf.organise.SettingType;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Priority;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.settings.Settings;

public class DatabaseLoader {
    private static final SimpleDateFormat _format = new SimpleDateFormat ("yyyy MMM dd");
    public static final String DATABASE_FILE = "database.xml";
    public static final String HISTORY_FILE = "history.txt";
    public static final String NORMAL_PRIORITY = "NORMAL";
    public static final String URGENT_PRIORITY = "URGENT";
    public static final String IMMEDIATE_PRIORITY = "IMMEDIATE";
    private final Builder _builder;
    private final EventBus _bus;

    public DatabaseLoader (EventBus bus) {
        _builder = new Builder ();
        _bus = bus;
    }

    public static File chooseDatabaseDirectory (Component gui) {
        JFileChooser chooser = new JFileChooser ();
        File file = null;
        int choice;

        chooser.setDialogType (JFileChooser.OPEN_DIALOG);
        chooser.setDialogTitle ("Select database directory");
        chooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
        choice = chooser.showDialog (gui, "Select");
        if (choice == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile ();
            Settings.get ().saveFile (SettingType.databaseDirectory, file);
        }

        return file;
    }
    
    public static boolean getBoolean (Node element, String path) {
        String text = getText (element, path, true);

        if (text != null) {
            return Boolean.valueOf (text);
        } else {
            return false;
        }
    }
    
    public static Date getDate (Node element, String path) {
        try {
            String text = getText (element, path, false);
            Date date = _format.parse (text);
            
            return date;
        } catch (ParseException ex) {
            return null;
        }
        
    }

    public static int getInteger (Node element, String path) {
        String text = getText (element, path, true);

        if (text != null) {
            return Integer.valueOf (text);
        } else {
            return -1;
        }
    }

    public static Priority getPriority (Node element, String path) {
        String text = getText (element, path, false);

        switch (text) {
            case NORMAL_PRIORITY:
                return Priority.normal;
            case URGENT_PRIORITY:
                return Priority.urgent;
            case IMMEDIATE_PRIORITY:
                return Priority.immediate;
        }
        System.err.println ("Unrecognised priority: " + text);
        return Priority.normal;
    }

    public static String getText (Node element, String path, boolean mayNotExist) {
        Nodes results = element.query (path);

        if (results.size () > 0) {
            return results.get (0).getValue ();
        } else {
            if (mayNotExist) {
                return null;
            } else {
                return "";
            }
        }
    }

    public static boolean isDatabaseDirectoryKnown () {
        return Settings.get ().getFilePath (SettingType.databaseDirectory) != null;
    }

    public void loadDatabase (Database database) throws AccessException {
        Path source = Settings.get ().getFilePath (SettingType.databaseDirectory);
        
        if (!Files.exists (source)) {
            throw new AccessException ("Database file not found");
        }
        loadDatabase (database, source.resolve (DATABASE_FILE));
    }

    public void loadDatabase (Database database, Path source) throws AccessException {
        try (BufferedReader in = Files.newBufferedReader (source, StandardCharsets.UTF_8);) {
            Document document = _builder.build (in);
            String version;

            database.clear ();
            version = getText (document, "/database/@version", true);
            switch (version) {
                case "0.2":
                    new DatabaseLoaderV02 (_bus).loadDatabase (database, document);
                    break;
                case "0.3":
                    new DatabaseLoaderV03 (_bus).loadDatabase (database, document);
                    break;
                default:
                    System.err.println ("Unknown version: " + version);
                    throw new Error ("Unknown version: " + version);
            }
        } catch (IOException | ParsingException ex) {
            throw new AccessException (ex);
        }
    }
}
