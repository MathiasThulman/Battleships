package Battleships;

import java.util.ArrayList;

public class Ship {
    ShipModel type;
    boolean sunk;
    int lifeCounter;

    public Ship(ShipModel type) throws GameException {
        this.type = type;
        sunk = false;
        lifeCounter = this.getShipSize();
    }

    public ShipModel getType() {
        return type;
    }

   public int getShipSize() throws GameException {
       switch(this.type){
           case SUBMARINE: return 2;
           case DESTROYER: return 3;
           case BATTLECRUISER: return 4;
           case AIRCRAFTCARRIER: return 5;
       }
       throw new GameException("ship does not exist");
   }

    public void setType(ShipModel type) {
        this.type = type;
    }

    public ArrayList<Ship> creatShips(String playerName){
        return null;
    }

    boolean gotHit(){
        lifeCounter--;
        if (lifeCounter <= 0) {
            return true;    //when sunken
        }
        return false;   //if hit bot not sunken
    }
}
