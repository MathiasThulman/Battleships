package Battleships;

import java.io.IOException;

public interface BattleShipsLocalBoard extends Battleships{

    /**
     * placeShip only for local board, no playerObject has to be given since it knows its local
     * @param ship Shipobject to be placed
     * @param position where it should be placed
     * @param horizontal horizontal or vertical
     * @throws StatusException
     * @throws GameException
     */
    void placeShip(Ship ship, BattleshipsPosition position, boolean horizontal) throws StatusException, GameException, InterruptedException, IOException;

    /**
     * same with bomb, only the position needs to be given since we know we are local
     * @param position
     * @return
     */
    Outcome bomb(BattleshipsPosition position) throws GameException, StatusException, IOException;

    /**
     *
     * @return tell me the status the gameEngine is in
     */
    Status getStatus();

    /**
     * tell if i am active or not
     * @return
     */
    boolean isActive();

    /**
     * tell me if i won
     * @return
     */
    boolean hasWon();

    /**
     * tell me if i lost
     * @return
     */
    boolean hasLost();

    /**
     * add a listener
     * @param changeListener
     */
    void subscribeChangeListener(LocalBoardChangeListener changeListener);
}
