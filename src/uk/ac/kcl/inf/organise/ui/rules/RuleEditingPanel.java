package uk.ac.kcl.inf.organise.ui.rules;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.rules.Reaction;
import uk.ac.kcl.inf.organise.rules.Rule;
import uk.ac.kcl.inf.organise.rules.Trigger;

public class RuleEditingPanel extends JPanel implements ActionListener, ItemsListener {
    private final TriggerChoicesPanel _triggerChoices;
    private final ReactionChoicesPanel _reactionChoices;
    private final TaskRuleList _taskRules;
    private final ItemListPanel<Trigger> _selectedTriggers;
    private final ItemListPanel<Reaction> _selectedReactions;
    private final JCheckBox _requiresAll;
    private final JButton _create;
    private final Database _database;
    private Task _task;
    
    public RuleEditingPanel (Database database, EventBus bus) {
        JLabel rulesHeading = new JLabel ("Rules");
        JLabel creator = new JLabel ("Create new rule");
        JLabel selected = new JLabel ("Selected components");
        
        _task = null;
        _database = database;
        
        setLayout (new MigLayout ("insets 0 0 0 0", "[0:0,grow 5,fill][0:0,grow 5,fill]", "[pref!][0:0,grow 5,fill][pref!][0:0,grow 5,fill][pref!][pref!][0:0,grow 5,fill][pref!]"));
        
        _taskRules = new TaskRuleList (database, bus);
        _triggerChoices = new TriggerChoicesPanel (this, bus);
        _reactionChoices = new ReactionChoicesPanel (this, database);
        _selectedTriggers = new ItemListPanel (true, false);
        _selectedReactions = new ItemListPanel (true, false);
        _create = new JButton ("Create rule from selected");
        _requiresAll = new JCheckBox ("Requires all triggers");
        
        rulesHeading.setHorizontalAlignment (JLabel.CENTER);
        creator.setHorizontalAlignment (JLabel.CENTER);
        selected.setHorizontalAlignment (JLabel.CENTER);

        add (rulesHeading, "span 2,wrap");
        add (_taskRules, "span 2,wrap");
        add (creator, "span 2,wrap");
        add (_triggerChoices);
        add (_reactionChoices, "wrap");
        add (selected, "span 2,wrap");
        add (_requiresAll, "wrap");
        add (new JScrollPane (_selectedTriggers));
        add (new JScrollPane (_selectedReactions), "wrap");
        add (_create, "span 2");
        
        _create.addActionListener (this);
        _taskRules.addItemsListener (this);
        _selectedTriggers.addItemsListener (this);
        _selectedReactions.addItemsListener (this);
    }
    
    public void addReaction (Reaction reaction) {
        _selectedReactions.addItem (reaction);
    }
    
    @Override
    public void actionPerformed (ActionEvent createClicked) {
        boolean requiresAll = _requiresAll.isSelected ();
        Rule rule = new Rule (_task, requiresAll);
        
        for (Object trigger : _selectedTriggers.getItems ()) {
            rule.addTrigger ((Trigger) trigger);
        }
        for (Object reaction : _selectedReactions.getItems ()) {
            rule.addReaction ((Reaction) reaction);
        }
        _database.addRule (rule);
    }

    public void addTrigger (Trigger trigger) {
        _selectedTriggers.addItem (trigger);
    }

    @Override
    public void delete (Object item, ItemListPanel list) {
        if (list == _taskRules) {
            _database.deleteRule ((Rule) item);
        }
    }

    @Override
    public void edit (Object item, ItemListPanel list) {
        list.delete (item);
    }
    
    public void open (Task task) {
        _task = task;
        _reactionChoices.open (task);
        _triggerChoices.open (task);
        _taskRules.open (task);
    }
}
