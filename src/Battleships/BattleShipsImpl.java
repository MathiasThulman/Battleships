package Battleships;

import Network.GameSessionEstablishedListener;
import view.BattleshipsStreamView;
import view.PrintStreamView;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

public class BattleShipsImpl implements Battleships, GameSessionEstablishedListener, BattleShipsLocalBoard {
    private Status status = Status.PLACESHIPS;
    public Player player1;
    public Player player2 = new Player("enemy player");
    private static final int DIMENSIONS = 10;
    public Ship[] fleet;
    private BattleshipsProtocolEngine protocolEngine;
    private PlayerRole localPlayerRole;
    private Semaphore semaphore = new Semaphore(1);

    {
        try {
            fleet = buildShips();
        } catch (GameException e) {
            e.printStackTrace();
        }
    }

    public BattleShipsImpl(String localPlayerName) {
        this.player1 = new Player(localPlayerName);
    }

    //pick player not needed anymore, happens in the constructor
/*
    @Override
    public void pickPlayer(String playerName) throws GameException, StatusException {
        if(this.status != Status.PICKPLAYER){
            throw new StatusException("pick player but wrong Status");
        }

        if(playerName == null || playerName.isEmpty()){
            throw new GameException("player name must contain at least one symbol");
        }
        else if (player1.getName() != null && player2.getName() != null) {
            throw new GameException("maximum amount of players reached");
        }
        else if (player1.getName() != null){
            if(playerName.equals(player1.getName())){
                throw new GameException("player name already taken, please choose a different one");
            } else {
                player2.setName(playerName);
                status = Status.PLACESHIPS; // when both parties have picked their name, change status to place ships
            }
        } else {
            player1.setName(playerName);
        }
    }*/

    public BattleshipsStreamView getPrintStreamView(){
        return new BattleshipsStreamView(this.shipOcean[0], this.hitOcean[0]);
    }


    public Ship[] buildShips() throws GameException{
        Ship[] fleet = new Ship[9];
        for(int i = 0; i < fleet.length; i++) {
            switch (i) {
                case 0: fleet[i] = new Ship(ShipModel.AIRCRAFTCARRIER); break;
                case 1, 2: fleet[i] = new Ship(ShipModel.BATTLECRUISER); break;
                case 3, 4 ,5: fleet[i] = new Ship(ShipModel.DESTROYER); break;
                case 6, 7, 8: fleet[i] = new Ship(ShipModel.SUBMARINE); break;
            }
        }
        return fleet;
    }

    private Ship[][][] shipOcean = new Ship[2][DIMENSIONS][DIMENSIONS];
    private BattleshipsPosition[][][] hitOcean = new BattleshipsPosition[2][DIMENSIONS][DIMENSIONS];

    private int getFleetPosition(Ship ship) throws GameException {          // builds all ship object in an array for later access
        switch(ship.getShipSize()){
            case 5 : return 0;
            case 4 : return 1;
            case 3 : return 2;
            case 2 : return 3;
        }
        throw new GameException("Type of ship not found");
    }

    @Override
    public void placeShip(Ship ship, BattleshipsPosition position, boolean horizontal) throws StatusException, GameException, InterruptedException, IOException {
        this.placeShip(0, ship, position, horizontal);
    }

    @Override
    public void placeShip(int playerInt, Ship ship, BattleshipsPosition position, boolean horizontal) throws GameException, StatusException, InterruptedException, IOException {
        //playerInt 0 = local, playerInt 1 = enemy
        if(this.status != Status.PLACESHIPS){
            throw new StatusException("place ship but wrong status");
        }

        int xCord = this.xCoordinateToInt(position.getxCoordinate());
        int yCord = position.getyCoordinate();

        if(xCord > 9 || yCord > 9){
            throw new GameException("destination out of bounds");
        }
        if( horizontal && ((xCord + ship.getShipSize() - 1) > 9)){
            throw new GameException("Ship will be out of bounds");
        }
        if(!horizontal && (yCord + ship.getShipSize() - 1) > 9){
            throw new GameException("Ship will be out of bounds");
        }

        if (intToPlayer(playerInt).getFleetCount()[getFleetPosition(ship)] <= 0) {               //asks if specific type of ship is still avialable
            throw new GameException("No more types of this Ship left");
        }

        semaphore.acquire();        // lock this part since both parties would acces the ocean array at the same time
        if(horizontal){             //checks if the space is already occuupied
            for(int i = 0; i < ship.getShipSize(); i++) {
                if (shipOcean[playerInt][xCord + i][yCord] != null) {
                    throw new GameException("this space is already occupied");
                }
            }
        } else {
            for(int i = 0; i < ship.getShipSize(); i++) {
                if(shipOcean[playerInt][xCord][yCord + i] != null){
                    throw new GameException("this space is already occupied");
                }
            }
        }

        if(horizontal){     //places the ship when the space if not occupied
            for(int i = 0; i < ship.getShipSize(); i++) {
                if (shipOcean[playerInt][xCord + i][yCord] != null) {
                    throw new GameException("this space is already occupied");
                } else {
                    shipOcean[playerInt][xCord + i][yCord] = ship;
                }
            }
        } else {
            for(int i = 0; i < ship.getShipSize(); i++) {
                if(shipOcean[playerInt][xCord][yCord + i] != null){
                    throw new GameException("this space is already occupied");
                } else {
                    shipOcean[playerInt][xCord][yCord + i] = ship;
                }
            }
        }
        semaphore.release();
        intToPlayer(playerInt).getFleetCount()[getFleetPosition(ship)]--;    //counts fleet down in fleet array
        this.protocolEngine.placeShip(1, ship, position, horizontal);  //TODO is this the right spot?

        if(player1.getFleetCount()[0] == 0 && player2.getFleetCount()[0] == 0 &&
                player1.getFleetCount()[1] == 0 && player2.getFleetCount()[1] == 0 &&
                player1.getFleetCount()[2] == 0 && player2.getFleetCount()[2] == 0 &&
                player1.getFleetCount()[3] == 0 && player2.getFleetCount()[3] == 0){
            if(this.localPlayerRole == PlayerRole.FIRST){
                this.status = Status.ACTIVE1;
            } else {
                this.status = Status.ACTIVE2;
            }
        }
    }

    private Outcome checkOcean(int xCord, int yCord, int playerInt,int enemyInt, Player enemy) throws GameException {
        if(hitOcean[playerInt][xCord][yCord] != null){              //checks in your ocean if position has already been bombed
            throw new GameException ("you have bombed this position already");
        }
        if(shipOcean[enemyInt][xCord][yCord] != null){                        // check if there is a ship in the enemy ocean at position
            hitOcean[playerInt][xCord][yCord] = new BattleshipsPosition(xCoordinateToString(xCord), yCord);     //create an objekt in players hitOcean so he cannot bomb same place twice
            hitOcean[playerInt][xCord][yCord].setStatus(PositionStatus.HIT);
            if(shipOcean[enemyInt][xCord][yCord].gotHit()) {
                System.out.println("Ship sunk matey!");
                enemy.reduceLife();                            //reduces the players life count on sunken ship
                shipOcean[enemyInt][xCord][yCord] = null;       //deletes enemy ship position
            }
            return Outcome.HIT;
        }

        hitOcean[playerInt][xCord][yCord] = new BattleshipsPosition(xCoordinateToString(xCord), yCord);         //create an objekt in players hitOcean so he cannot bomb same place twice
        hitOcean[playerInt][xCord][yCord].setStatus(PositionStatus.MISS);
        return Outcome.MISS;
    }

    @Override
    public Outcome bomb(BattleshipsPosition position) throws GameException, StatusException, IOException {
        return this.bomb(0, position);
    }

    @Override
    public Outcome bomb(int playerInt, BattleshipsPosition position) throws GameException, StatusException, IOException {
        if(this.status != Status.ACTIVE1 && this.status != Status.ACTIVE2){
            throw new StatusException("bomb but wrong status");
        }

        int xCord = this.xCoordinateToInt(position.getxCoordinate());
        int yCord = position.getyCoordinate();

        if(xCord > 9 || yCord > 9){                 //first checks if bombed position is in bounds
            throw new GameException("bomb destination out of bounds");
        }

        if(this.status == Status.ACTIVE1 && !(intToPlayer(playerInt) == player1)){              //checks its player 1s turn
            throw new StatusException("it is not your turn");
        }
        else if (this.status == Status.ACTIVE1) {
            if(checkOcean(xCord, yCord, 0, 1, player2) == Outcome.MISS){
                this.status = Status.ACTIVE2;
                this.protocolEngine.bomb(1, position);
                return Outcome.MISS;
            } else{
                this.protocolEngine.bomb(1, position);
                return Outcome.HIT;
            }
        }
        else if(this.status == Status.ACTIVE2 && !(intToPlayer(playerInt) == player2)){
            throw new StatusException("it is not your turn");
        }
        else if(this.status == Status.ACTIVE2){
            if(checkOcean(xCord, yCord, 1, 0, player1) == Outcome.MISS){
                this.status = Status.ACTIVE1;
                this.protocolEngine.bomb(1, position);
                return Outcome.MISS;
            } else{
                this.protocolEngine.bomb(1, position);
                return Outcome.HIT;
            }
        }

        if(player1.getLifeCount() == 0){
            System.out.println(player2.getName() + "has won!");
            this.status = Status.PLAYER2WON;
            return Outcome.WON;
        } else if (player2.getLifeCount() == 0){
            System.out.println(player1.getName() + "has won!");
            this.status = Status.PLAYER1WON;
            return Outcome.WON;
        }
        throw new GameException ("something went wrong with the bombing");
    }

    private int xCoordinateToInt(String xCoordinate) throws GameException{
        switch (xCoordinate) {
            case "A", "a": return 0;
            case "B", "b": return 1;
            case "C", "c": return 2;
            case "D", "d": return 3;
            case "E", "e": return 4;
            case "F", "f": return 5;
            case "G", "g": return 6;
            case "H", "h": return 7;
            case "I", "i": return 8;
            case "J", "j": return 9;
        }
        throw new GameException("position not in the ocean");
    }

    private String xCoordinateToString(int xCoordinate) throws GameException{
        switch (xCoordinate) {
            case 0 : return "A";
            case 1: return "B";
            case 2: return "C";
            case 3 : return "D";
            case 4: return "E";
            case 5: return "F";
            case 6: return "G";
            case 7: return "H";
            case 8: return "I";
            case 9: return "J";
        }
        throw new GameException("position not in the ocean");
    }

    public Player getPlayer1() {
        return this.player1;
    }
    public Player getPlayer2() {
        return this.player2;
    }

    public Ship[] getFleet() {
        return fleet;
    }

    public Player intToPlayer(int playerInt) throws GameException {
        switch(playerInt){
            case 0: return this.getPlayer1();
            case 1: return this.getPlayer2();
        }
        throw new GameException("Player Int now found");
    }

    public void setProtocolEngine(BattleshipsProtocolEngine protocolEngine) {
        this.protocolEngine = protocolEngine;
        this.protocolEngine.subscribeGameSessionEstablishedListener(this);
    }

    public Status getStatus() {
        return this.status;
    }

    @Override
    public boolean isActive() {
        return (this.status == Status.ACTIVE1 || this.status == Status.PLACESHIPS);
    }

    @Override
    public boolean hasWon() {
        return (this.status == Status.PLAYER1WON);
    }

    @Override
    public boolean hasLost() {
        return (this.status == Status.PLAYER2WON);
    }

    LinkedList<LocalBoardChangeListener> localBoardChangeListenerList = new LinkedList<LocalBoardChangeListener>();
    @Override
    public void subscribeChangeListener(LocalBoardChangeListener changeListener) {
        this.localBoardChangeListenerList.add(changeListener);
    }

    void notifyBoardChanged(){
        //are there even listeners?
        if(this.localBoardChangeListenerList == null || this.localBoardChangeListenerList.isEmpty()){
            return;
        }

        //if yes notify them of a change
        (new Thread(new Runnable() {
            @Override
            public void run() {
                for(LocalBoardChangeListener listener : BattleShipsImpl.this.localBoardChangeListenerList){
                    listener.changed();
                }
            }
        })).start();
    }

    public void setEnemyName(String enemyName){
        this.getPlayer2().setName(enemyName);
    }



    @Override
    public void gameSessionEstablished(boolean oracle, String partnerName) {
        System.out.println(this.player1.getName() + ": gameSession estable with" + partnerName + " | " + oracle);

        this.localPlayerRole = oracle ? PlayerRole.FIRST : PlayerRole.SECOND;

    }

}
