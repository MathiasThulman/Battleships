package Battleships;

import org.junit.Assert;
import org.junit.Test;

public class BattleShipsTest {
    public static final String BISMARCK = "Bismarck";
    public static final String WARSPITE = "Warspite";
    public static final String JOSH = "Josh";
    public static Ship BATTLECRUISER1;
    public static Ship BATTLECRUISER2;
    public static Ship DESTROYER1;
    public static Ship DESTROYER2;
    public static Ship DESTROYER3;
    public static Ship SUBMARINE1;
    public static Ship SUBMARINE2;
    public static Ship SUBMARINE3;
    public static Ship AIRCRAFTCARRIER;
    static {
        try {
            Ship BATTLECRUISER1 = new Ship(ShipModel.BATTLECRUISER);
            Ship BATTLECRUISER2 = new Ship(ShipModel.BATTLECRUISER);
            Ship DESTROYER1 = new Ship(ShipModel.DESTROYER);
            Ship DESTROYER2 = new Ship(ShipModel.DESTROYER);
            Ship DESTROYER3 = new Ship(ShipModel.DESTROYER);
            Ship SUBMARINE1 = new Ship(ShipModel.SUBMARINE);
            Ship SUBMARINE2 = new Ship(ShipModel.SUBMARINE);
            Ship SUBMARINE3 = new Ship(ShipModel.SUBMARINE);
            Ship AIRCRAFTCARRIER = new Ship(ShipModel.AIRCRAFTCARRIER);
        } catch (GameException e) {
            e.printStackTrace();
        }
    }
    public static final Player zoop = new Player("zoop");
    public static final BattleshipsPosition BSP1 = new BattleshipsPosition("A", 4);
    public static final BattleshipsPosition BSP2 = new BattleshipsPosition("D", 2);
    public static final BattleshipsPosition BSP3 = new BattleshipsPosition("E", 5);
    public static final BattleshipsPosition BSP4 = new BattleshipsPosition("J", 3);
    public static final BattleshipsPosition BSP5 = new BattleshipsPosition("A", 9);
    public static final BattleshipsPosition BSP6 = new BattleshipsPosition("B", 9);
    public static final BattleshipsPosition BSP7 = new BattleshipsPosition("C", 9);
    public static final BattleshipsPosition BSP8 = new BattleshipsPosition("D", 9);
    public static final BattleshipsPosition BSPR1 = new BattleshipsPosition("H", 8);
    public static final BattleshipsPosition BSPI1 = new BattleshipsPosition("X", 4);
    public static final BattleshipsPosition BSPI2 = new BattleshipsPosition("B", 19);
    public static final BattleshipsPosition BSPI3 = new BattleshipsPosition("O", 230);

    private BattleShipsImpl getBattleships() {
        return new BattleShipsImpl("default player");
    }
/*
    private void createBattleField(BattleShipsImpl bs) throws GameException, StatusException {

        Ship[] fleet = bs.buildShips();
        bs.placeShip(bs.getPlayer1(), fleet[0], new BattleshipsPosition("B",1), true);
        bs.placeShip(bs.getPlayer1(), fleet[1], new BattleshipsPosition("H", 3), false);
        bs.placeShip(bs.getPlayer1(), fleet[2], new BattleshipsPosition("A", 5), true);
        bs.placeShip(bs.getPlayer1(), fleet[3], new BattleshipsPosition("F", 7), false);
        bs.placeShip(bs.getPlayer1(), fleet[4], new BattleshipsPosition("J", 5), false);
        bs.placeShip(bs.getPlayer1(), fleet[5], new BattleshipsPosition("B", 3), true);
        bs.placeShip(bs.getPlayer1(), fleet[6], new BattleshipsPosition("D",8), false);
        bs.placeShip(bs.getPlayer1(), fleet[7], new BattleshipsPosition("H", 8), false);
        bs.placeShip(bs.getPlayer1(), fleet[8], new BattleshipsPosition("A", 9), true);

        bs.placeShip(bs.getPlayer2(), fleet[0], new BattleshipsPosition("D", 5),true);
        bs.placeShip(bs.getPlayer2(), fleet[1], new BattleshipsPosition("A", 9),true);
        bs.placeShip(bs.getPlayer2(), fleet[2], new BattleshipsPosition("J", 6),false);
        bs.placeShip(bs.getPlayer2(), fleet[3], new BattleshipsPosition("B", 3),false);
        bs.placeShip(bs.getPlayer2(), fleet[4], new BattleshipsPosition("G", 1),true);
        bs.placeShip(bs.getPlayer2(), fleet[5], new BattleshipsPosition("I", 2),false);
        bs.placeShip(bs.getPlayer2(), fleet[6], new BattleshipsPosition("B", 0),false);
        bs.placeShip(bs.getPlayer2(), fleet[7], new BattleshipsPosition("G", 8),false);
        bs.placeShip(bs.getPlayer2(), fleet[8], new BattleshipsPosition("C", 7),true);
  //      return bs;
    }

//none of this works after tcp protocol engine implementation
    /*
    ////pickPlayer Tests
    @Test
    public void pickPlayerValid1() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        Assert.assertEquals("Bismarck", bs.getPlayer1().getName());
        Assert.assertEquals("Warspite", bs.getPlayer2().getName());
    }

    @Test
    public void pickPlayerValid() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();

        bs.pickPlayer(WARSPITE);
        bs.pickPlayer(BISMARCK);

        Assert.assertEquals("Bismarck", bs.getPlayer2().getName());
        Assert.assertEquals("Warspite", bs.getPlayer1().getName());
    }

    @Test(expected = StatusException.class)
    public void pickPlayerInvalid3x() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);
        bs.pickPlayer(JOSH);
    }

    @Test(expected = GameException.class)
    public void pickPlayerInValid1() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();

        bs.pickPlayer("");
    }

    @Test(expected = GameException.class)
    public void pickPlayerInvalid2() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(BISMARCK);
    }

    @Test(expected = StatusException.class)
    public void pickPlayerStatusTest() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();
        Ship[] fleet = bs.buildShips();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        bs.placeShip(bs.getPlayer1(), fleet[8], BSP1, true);
        bs.placeShip(bs.getPlayer2(), fleet[7], BSP3, true);

        bs.pickPlayer(BISMARCK);
    }

    ////placeShip Tests

    @Test(expected = StatusException.class)
    public void placeShipWrongStatus() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();
        Ship[] fleet = bs.buildShips();

        bs.placeShip(zoop, fleet[5], BSP1,true);
    }

    @Test
    public void placeShipTestValid1() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();
        Ship[] fleet = bs.buildShips();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        bs.placeShip(bs.getPlayer1(), fleet[8], BSP1,true);
    }

    @Test
    public void placeShipTestValid2Edge() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();
        Ship[] fleet = bs.buildShips();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        //TODO more ships to edges once map, ships and placing is finished
        bs.placeShip(bs.getPlayer2(), fleet[8], BSPR1,true);
    }

    @Test(expected = GameException.class)
    public void placeShipTestOutOfBounds1() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();
        Ship[] fleet = bs.buildShips();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        bs.placeShip(bs.getPlayer2(), fleet[8], BSPI1,true);
    }

    @Test(expected = GameException.class)
    public void placeShipTestOutOfBounds2() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();
        Ship[] fleet = bs.buildShips();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        bs.placeShip(bs.getPlayer1(), fleet[5], BSPI3,true);
    }

    @Test(expected = GameException.class)
    public void placeShipTestOverlap1() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();
        Ship[] fleet = bs.buildShips();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        bs.placeShip(bs.getPlayer1(), fleet[8], BSP1,true);
        bs.placeShip(bs.getPlayer1(), fleet[8], BSP1,true);
    }

    @Test(expected = GameException.class)
    public void placeShipTestOverlap2() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();
        Ship[] fleet = bs.buildShips();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        //TODO after creating field and ships make test with overlap that doesnt invlove the same position
        bs.placeShip(bs.getPlayer1(), fleet[2], BSP3,true);
        bs.placeShip(bs.getPlayer1(), fleet[8], new BattleshipsPosition("E", 4),false);
    }

    @Test(expected = GameException.class)
    public void placeShipTestOverlap3() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();
        Ship[] fleet = bs.buildShips();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        //TODO after creating field and ships make test with overlap that doesnt invlove the same position
        bs.placeShip(bs.getPlayer1(), fleet[0], new BattleshipsPosition("G", 3),false);
        bs.placeShip(bs.getPlayer1(), fleet[4], new BattleshipsPosition("E", 7),true);
    }

    @Test(expected = GameException.class)
    public void placeShipTestShipsNoTypeOfShipLeft() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();
        Ship[] fleet = bs.buildShips();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        bs.placeShip(bs.getPlayer1(), fleet[1], BSP1,true);
        bs.placeShip(bs.getPlayer1(), fleet[2], BSP2,true);
        bs.placeShip(bs.getPlayer1(), fleet[2], BSP3,true);
    }

    ////bomb Tests

    @Test(expected = StatusException.class)
    public void bombStatusTest1() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        bs.bomb(bs.getPlayer1(), BSP1);
    }

    @Test(expected = StatusException.class)
    public void bombStatusTest2() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();

        bs.bomb(bs.getPlayer1(), BSP1);
    }

    @Test
    public void bombTestValidNoHit() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        createBattleField(bs);

        bs.bomb(bs.getPlayer1(), BSP1);
        bs.bomb(bs.getPlayer2(), BSP1);
    }

    @Test
    public void bombTestValidHit() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        createBattleField(bs);

        bs.bomb(bs.getPlayer1(), BSP3);
        bs.bomb(bs.getPlayer1(), BSP1);
    }

    @Test(expected = GameException.class)
    public void bombTestOutOfBounds1() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        createBattleField(bs);

        bs.bomb(bs.getPlayer1(), BSPI1);
    }

    @Test(expected = GameException.class)
    public void bombTestOutOfBounds2() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        createBattleField(bs);

        bs.bomb(bs.getPlayer1(), BSPI3);
    }

    @Test(expected = StatusException.class)
    public void bombTestNotYourTurn1() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        createBattleField(bs);

        bs.bomb(bs.getPlayer1(), BSP4);
        bs.bomb(bs.getPlayer1(), BSP2);
    }

    @Test(expected = StatusException.class)
    public void bombTestNotYourTurnOnHit() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        createBattleField(bs);

        bs.bomb(bs.getPlayer1(), BSP3);
        bs.bomb(bs.getPlayer2(), BSP1);
    }

    @Test(expected = GameException.class)
    public void bombTestAlreadyBombed() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        createBattleField(bs);

        bs.bomb(bs.getPlayer1(), BSP2);
        bs.bomb(bs.getPlayer2(), BSP1);
        bs.bomb(bs.getPlayer1(), BSP2);
    }

    @Test
    public void bombTestShipSunk() throws GameException, StatusException {
        BattleShipsImpl bs = this.getBattleships();

        bs.pickPlayer(BISMARCK);
        bs.pickPlayer(WARSPITE);

        createBattleField(bs);

        bs.bomb(bs.getPlayer1(),BSP5);
        bs.bomb(bs.getPlayer1(),BSP6);
        bs.bomb(bs.getPlayer1(),BSP7);
        bs.bomb(bs.getPlayer1(),BSP8);

        Assert.assertEquals(8, bs.getPlayer2().getLifeCount());

    }*/
}
