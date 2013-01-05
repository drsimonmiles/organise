package uk.ac.kcl.inf.organise.rules;

public abstract class Trigger {
    private Rule _rule;
    
    public Trigger () {
        _rule = null;
    }
    
    void registerRule (Rule rule) {
        _rule = rule;
    }
    
    protected void triggered () {
        if (_rule != null) {
            _rule.triggered (this);
        }
    }
}
