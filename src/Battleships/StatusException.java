package Battleships;

/**
 * used if methods are called in the wrong status
 */
public class StatusException extends Exception {
    public StatusException() { super(); }
    public StatusException(String message) { super(message); }
    public StatusException(String message, Throwable t) { super(message, t);}
}
