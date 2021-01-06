package Battleships;

public class Player {
    public String name;
    public int[] fleetCounter = {1,2,3,3};
    public int lifeCount = 9;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int[] getFleetCount() {
        return fleetCounter;
    }

    public int getLifeCount() {
        return lifeCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void reduceLife() {
        this.lifeCount--;
    }

}
