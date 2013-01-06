package uk.ac.kcl.inf.organise.rules;

public class PostponeRuleReaction implements Reaction {
    private int _days;
    
    public PostponeRuleReaction (int days) {
        _days = days;
    }

    public int getDays () {
        return _days;
    }
    
    @Override
    public void perform (Rule rule) {
        for (Trigger trigger : rule.getTriggers ()) {
            if (trigger instanceof OnOrAfterTrigger) {
                ((OnOrAfterTrigger) trigger).postpone (_days);
            }
        }
    }

    @Override
    public String toString () {
        return "Postpone rule by " + _days + " days";
    }
}
