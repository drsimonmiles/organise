package uk.ac.kcl.inf.organise.rules;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import uk.ac.kcl.inf.organise.data.Task;

public class Rule {
    private final Task _owner;
    private final List<Trigger> _triggers;
    private final List<Reaction> _reactions;
    private final boolean _requiresAll;
    private final Set<Trigger> _triggered;

    public Rule (Task owner, boolean requiresAll) {
        _owner = owner;
        _triggers = new LinkedList<> ();
        _reactions = new LinkedList<> ();
        _triggered = new HashSet<> ();
        _requiresAll = requiresAll;
    }

    public void addReaction (Reaction reaction) {
        _reactions.add (reaction);
    }

    public void addTrigger (Trigger trigger) {
        _triggers.add (trigger);
        trigger.registerRule (this);
    }

    public boolean doesRequireAll () {
        return _requiresAll;
    }

    public Task getOwner () {
        return _owner;
    }

    public List<Reaction> getReactions () {
        return _reactions;
    }

    public List<Trigger> getTriggers () {
        return _triggers;
    }

    public void react () {
        for (Reaction reaction : _reactions) {
            reaction.perform ();
        }
    }

    public void triggered (Trigger trigger) {
        if (_requiresAll) {
            _triggered.add (trigger);
            if (_triggered.size () < _triggers.size ()) {
                return;
            }
        }
        react ();
    }
}
