package Battleships;

import java.awt.*;
import java.io.IOException;

public interface Battleships {
    /**
     * creats a player from the given Sring
     * @param player
     * @throws GameException when the name already exists
     * @throws StatusException when method is called in wrong status
     */
    //void pickPlayer(String player) throws GameException, StatusException;

    /**
     * places a ship on your battlefield
     * @param ship type of ship you want to place
     * @param position
     * @return true when all ships have been place ; false when you still have ships to place
     * @throws Exception when out of bounds, already occupied, invalid input
     */
    void placeShip(int playerInt,Ship ship, BattleshipsPosition position, boolean horizontal) throws GameException, StatusException, IOException, InterruptedException;

    /**
     * attack the given position on the enemy field
     * @param position
     * @throws Exception
     * @return HIT on hit and MISS on miss
     */
    Outcome bomb(int playerInt, BattleshipsPosition position) throws GameException, StatusException, IOException;
}
