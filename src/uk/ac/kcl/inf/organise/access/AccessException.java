package uk.ac.kcl.inf.organise.access;

public class AccessException extends Exception {
    public AccessException (Throwable cause) {
        super (cause);
    }
    
    public AccessException (String reason) {
        super (reason);
    }
}
