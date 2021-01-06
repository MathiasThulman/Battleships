package Battleships;

/**
 * used when input invalid to the game has been called
 */
public class GameException extends Exception{
    public GameException() { super(); }
    public GameException(String message) { super(message); }
    public GameException(String message, Throwable t) { super(message, t);}
}
