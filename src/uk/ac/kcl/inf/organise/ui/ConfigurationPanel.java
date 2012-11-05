package uk.ac.kcl.inf.organise.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import net.miginfocom.swing.MigLayout;
import uk.ac.kcl.inf.organise.SettingType;
import uk.ac.kcl.inf.settings.Settings;

public class ConfigurationPanel extends JPanel {
    private JTextField _database;
    private JTextField _summary;
    
    public ConfigurationPanel () {
        _database = new JTextField (Settings.get ().getFilePath (SettingType.databaseDirectory).toString ());
        _summary = new JTextField (Settings.get ().getFilePath (SettingType.summaryDirectory).toString ());
        
        setLayout (new MigLayout ("insets 0 0 0 0", "[0:0,grow,fill]", ""));
        add (new JLabel ("Database directory"), "wrap");
        add (_database, "wrap");
        add (new JLabel ("Summary directory"), "wrap");
        add (_summary, "wrap");
    }
}
