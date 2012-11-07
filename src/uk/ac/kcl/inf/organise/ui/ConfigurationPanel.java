package uk.ac.kcl.inf.organise.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import uk.ac.kcl.inf.organise.SettingType;
import uk.ac.kcl.inf.organise.events.EventBus;

public class ConfigurationPanel extends JPanel {
    private final PathSettingField _database;
    private final PathSettingField _summary;

    public ConfigurationPanel (EventBus bus) {
        _database = new PathSettingField (SettingType.databaseDirectory, bus);
        _summary = new PathSettingField (SettingType.summaryDirectory, bus);

        setLayout (new MigLayout ("insets 0 0 0 0", "[0:0,grow,fill]", ""));
        add (new JLabel ("Database directory"), "wrap");
        add (_database, "wrap");
        add (new JLabel ("Summary directory"), "wrap");
        add (_summary, "wrap");
    }
}
