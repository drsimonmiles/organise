package uk.ac.kcl.inf.organise.rules;

public class PostponeRuleReaction implements Reaction {
    private Rule _rule;
    private int _days;
    
    public PostponeRuleReaction (Rule rule, int days) {
        _rule = rule;
        _days = days;
    }

    @Override
    public void perform () {
        for (Trigger trigger : _rule.getTriggers ()) {
            if (trigger instanceof OnOrAfterTrigger) {
                ((OnOrAfterTrigger) trigger).postpone (_days);
            }
        }
    }
}
