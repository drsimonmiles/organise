package uk.ac.kcl.inf.organise.ui.rules;

import java.util.Date;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.rules.AllocateReaction;
import uk.ac.kcl.inf.organise.rules.CompletionTrigger;
import uk.ac.kcl.inf.organise.rules.OnOrAfterTrigger;
import uk.ac.kcl.inf.organise.rules.PostponeRuleReaction;

public class RuleEditingPanel extends JPanel implements ItemsListener {
    private final TriggerChoicesPanel _triggerChoices;
    private final ReactionChoicesPanel _reactionChoices;
    private final ItemListPanel _taskRules, _selectedTriggers, _selectedReactions;
    private final JButton _create;
    
    public RuleEditingPanel (Task task, EventBus bus) {
        JLabel rulesHeading = new JLabel ("Rules");
        JLabel creator = new JLabel ("Create new rule");
        JLabel selected = new JLabel ("Selected components");
        
        setLayout (new MigLayout ("insets 0 0 0 0", "[0:0,grow 5,fill][0:0,grow 5,fill]", "[pref!][0:0,grow 5,fill][pref!][0:0,grow 5,fill][pref!][0:0,grow 5,fill][pref!]"));
        
        _taskRules = new ItemListPanel (true, true);
        _triggerChoices = new TriggerChoicesPanel ();
        _reactionChoices = new ReactionChoicesPanel ();
        _selectedTriggers = new ItemListPanel (true, false);
        _selectedReactions = new ItemListPanel (true, false);
        _create = new JButton ("Create rule from selected");
        
        rulesHeading.setHorizontalAlignment (JLabel.CENTER);
        creator.setHorizontalAlignment (JLabel.CENTER);
        selected.setHorizontalAlignment (JLabel.CENTER);

        add (rulesHeading, "span 2,wrap");
        add (_taskRules, "span 2,wrap");
        add (creator, "span 2,wrap");
        add (_triggerChoices);
        add (_reactionChoices, "wrap");
        add (selected, "span 2,wrap");
        add (new JScrollPane (_selectedTriggers));
        add (new JScrollPane (_selectedReactions), "wrap");
        add (_create, "span 2");
        
        // TEST
        _selectedTriggers.addItem (new CompletionTrigger (task, bus));
        _selectedTriggers.addItem (new OnOrAfterTrigger (new Date (), bus));
        _selectedReactions.addItem (new AllocateReaction (task, 20));
        _selectedReactions.addItem (new PostponeRuleReaction (null, 5));
    }

    @Override
    public void delete (Object item, ItemListPanel list) {
    }

    @Override
    public void edit (Object item, ItemListPanel list) {
        list.delete (item);
    }
}
