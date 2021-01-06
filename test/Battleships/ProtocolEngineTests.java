package Battleships;

import Network.ProtocolEngine;
import Network.TCPStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;


public class ProtocolEngineTests {
    public static final String BISMARCK = "Bismarck";
    public static final String WARSPITE = "Warspite";
    public static final int PORTNUMER = 6666;
    public static final Player bismarck = new Player("Bismarck");
    public static final BattleshipsPosition BSP1 = new BattleshipsPosition("B",4);
    public static final BattleshipsPosition BSP2 = new BattleshipsPosition("E",5);
    private static final long TEST_THREAD_SLEEP_DURATION = 1000;
    private static int port = 0;

    private Battleships getBSEngine(InputStream is, OutputStream os, Battleships gameEngine){
        return new BattleshipsProtocolEngine (gameEngine, BISMARCK);
    }

    private int getPortNumber(){
        if(ProtocolEngineTests.port == 0){
            ProtocolEngineTests.port = PORTNUMER;
        } else {
            ProtocolEngineTests.port++;
        }
        System.out.println("use portnumber " + ProtocolEngineTests.port);
        return ProtocolEngineTests.port;
    }

    @Test
    public void integrationTestFullGame() throws GameException, StatusException, IOException, InterruptedException {
        // there are players in this test: Alice and Bob

        // create Alice's game engine
        BattleShipsImpl bismarckGameEngine = new BattleShipsImpl("Bismarck");    //TODO add local player to impl
        // create real protocol engine on Alice's side
        BattleshipsProtocolEngine bismarckProtocolEngine =
                new BattleshipsProtocolEngine(bismarckGameEngine, BISMARCK);

        bismarckGameEngine.setProtocolEngine(bismarckProtocolEngine);

        // create Bob's game engine
        BattleShipsImpl warspiteGameEngine = new BattleShipsImpl("Warspite");
        // create real protocol engine on Bob's side
        BattleshipsProtocolEngine warspiteProtocolEngine =
                new BattleshipsProtocolEngine(warspiteGameEngine, WARSPITE);

        warspiteGameEngine.setProtocolEngine(warspiteProtocolEngine);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                           setup tcp                                                    //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        int port = this.getPortNumber();
        // this stream plays TCP server role during connection establishment
        TCPStream bismarckSide = new TCPStream(port, true, "bismarckSide");
        // this stream plays TCP client role during connection establishment
        TCPStream warspiteSide = new TCPStream(port, false, "warspite");
        // start both stream
        bismarckSide.start(); warspiteSide.start();
        // wait until TCP connection is established
        warspiteSide.waitForConnection(); bismarckSide.waitForConnection();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                       launch protocol engine                                           //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // give protocol engines streams and launch
        bismarckProtocolEngine.handleConnection(bismarckSide.getInputStream(), bismarckSide.getOutputStream());
        warspiteProtocolEngine.handleConnection(warspiteSide.getInputStream(), warspiteSide.getOutputStream());

        // give it a moment - important stop this test thread - to threads must be launched
        System.out.println("give threads a moment to be launched");
        Thread.sleep(TEST_THREAD_SLEEP_DURATION);

        //play the game

        Ship[] fleet = bismarckGameEngine.buildShips();

        bismarckGameEngine.placeShip(0, fleet[0], new BattleshipsPosition("B",1), true);
        bismarckProtocolEngine.placeShip(0, fleet[0], new BattleshipsPosition("B",1), true);
        bismarckGameEngine.placeShip(0, fleet[1], new BattleshipsPosition("H", 3), false);
        bismarckProtocolEngine.placeShip(0, fleet[1], new BattleshipsPosition("H", 3), false);
        bismarckGameEngine.placeShip(0, fleet[2], new BattleshipsPosition("A", 5), true);
        bismarckProtocolEngine.placeShip(0, fleet[2], new BattleshipsPosition("A", 5), true);
        bismarckGameEngine.placeShip(0, fleet[3], new BattleshipsPosition("F", 7), false);
        bismarckProtocolEngine.placeShip(0, fleet[3], new BattleshipsPosition("F", 7), false);
        bismarckGameEngine.placeShip(0, fleet[4], new BattleshipsPosition("J", 5), false);
        bismarckProtocolEngine.placeShip(0, fleet[4], new BattleshipsPosition("J", 5), false);
        bismarckGameEngine.placeShip(0, fleet[5], new BattleshipsPosition("B", 3), true);
        bismarckProtocolEngine.placeShip(0, fleet[5], new BattleshipsPosition("B", 3), true);
        bismarckGameEngine.placeShip(0, fleet[6], new BattleshipsPosition("D",8), false);
        bismarckProtocolEngine.placeShip(0, fleet[6], new BattleshipsPosition("D",8), false);
        bismarckGameEngine.placeShip(0, fleet[7], new BattleshipsPosition("H", 8), false);
        bismarckProtocolEngine.placeShip(0, fleet[7], new BattleshipsPosition("H", 8), false);
        bismarckGameEngine.placeShip(0, fleet[8], new BattleshipsPosition("A", 9), true);
        bismarckProtocolEngine.placeShip(0, fleet[8], new BattleshipsPosition("A", 9), true);

        warspiteGameEngine.placeShip(0, fleet[0], new BattleshipsPosition("D", 5),true);
        warspiteProtocolEngine.placeShip(0, fleet[0], new BattleshipsPosition("D", 5),true);
        warspiteGameEngine.placeShip(0, fleet[1], new BattleshipsPosition("A", 9),true);
        warspiteProtocolEngine.placeShip(0, fleet[1], new BattleshipsPosition("A", 9),true);
        warspiteGameEngine.placeShip(0, fleet[2], new BattleshipsPosition("J", 6),false);
        warspiteProtocolEngine.placeShip(0, fleet[2], new BattleshipsPosition("J", 6),false);
        warspiteGameEngine.placeShip(0, fleet[3], new BattleshipsPosition("B", 3),false);
        warspiteProtocolEngine.placeShip(0, fleet[3], new BattleshipsPosition("B", 3),false);
        warspiteGameEngine.placeShip(0, fleet[4], new BattleshipsPosition("G", 1),true);
        warspiteProtocolEngine.placeShip(0, fleet[4], new BattleshipsPosition("G", 1),true);
        warspiteGameEngine.placeShip(0, fleet[5], new BattleshipsPosition("I", 2),false);
        warspiteProtocolEngine.placeShip(0, fleet[5], new BattleshipsPosition("I", 2),false);
        warspiteGameEngine.placeShip(0, fleet[6], new BattleshipsPosition("B", 0),false);
        warspiteProtocolEngine.placeShip(0, fleet[6], new BattleshipsPosition("B", 0),false);
        warspiteGameEngine.placeShip(0, fleet[7], new BattleshipsPosition("G", 8),false);
        warspiteProtocolEngine.placeShip(0, fleet[7], new BattleshipsPosition("G", 8),false);
        warspiteGameEngine.placeShip(0, fleet[8], new BattleshipsPosition("C", 7),true);
        warspiteProtocolEngine.placeShip(0, fleet[8], new BattleshipsPosition("C", 7),true);




        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                             tidy up                                                    //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        bismarckProtocolEngine.close();
        warspiteProtocolEngine.close();

        // stop test thread to allow operating system to close sockets
        Thread.sleep(TEST_THREAD_SLEEP_DURATION);

        // Thread.sleep(Long.MAX_VALUE); // debugging
    }

    //test not usable after TCP implementation
    /*
    @Test
    public void pickerPlayerTest1() throws GameException, StatusException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Battleships bsProtocollSender = this.getBSEngine(null, baos, null);
        bsProtocollSender.pickPlayer(BISMARCK);


        //simulate networt
        byte[] serializedBytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedBytes);

        BattleshipsReadTester bsReceiver = new BattleshipsReadTester();
        Battleships bsProtocolReceiver = this.getBSEngine(bais, null, bsReceiver);

        BattleshipsProtocolEngine bsEngine = (BattleshipsProtocolEngine) bsProtocolReceiver;
        bsEngine.read();

        Assert.assertTrue(bsReceiver.lastCallPick);
        Assert.assertEquals("Bismarck", bsReceiver.getPlayerName());
    }

    @Test
    public void placeShipTest() throws GameException, StatusException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Battleships bsProtocollSender = this.getBSEngine(null, baos, null);
        bsProtocollSender.placeShip(bismarck, new Ship(ShipModel.DESTROYER), BSP1, true);
//        BSPR.pickPlayer(WARSPITE);

        //simulate networt
        byte[] serializedBytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedBytes);

        BattleshipsReadTester bsReceiver = new BattleshipsReadTester();
        Battleships bsProtocolReceiver = this.getBSEngine(bais, null, bsReceiver);

        BattleshipsProtocolEngine bsEngine = (BattleshipsProtocolEngine) bsProtocolReceiver;
        bsEngine.read();

        Assert.assertTrue(bsReceiver.lastCallPlaceShip);
        Assert.assertEquals(BSP1.getxCoordinate(), bsReceiver.getBattleshipsPosition().getxCoordinate());
        Assert.assertEquals(BSP1.getyCoordinate(), bsReceiver.getBattleshipsPosition().getyCoordinate());
        Assert.assertEquals(3, bsReceiver.getShip().getShipSize());
    }

    @Test
    public void bombTest1() throws GameException, StatusException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Battleships bsProtocollSender = this.getBSEngine(null, baos, null);
        bsProtocollSender.bomb(bismarck, BSP2);

        //simulate networt
        byte[] serializedBytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedBytes);

        BattleshipsReadTester bsReceiver = new BattleshipsReadTester();
        Battleships bsProtocolReceiver = this.getBSEngine(bais, null, bsReceiver);

        BattleshipsProtocolEngine bsEngine = (BattleshipsProtocolEngine) bsProtocolReceiver;
        bsEngine.read();

        Assert.assertTrue(bsReceiver.lastCallBomb);
        Assert.assertEquals(BSP2.getxCoordinate(), bsReceiver.getBattleshipsPosition().getxCoordinate());
        Assert.assertEquals(BSP2.getyCoordinate(), bsReceiver.getBattleshipsPosition().getyCoordinate());
    }


    private class BattleshipsReadTester implements Battleships{
        private boolean lastCallPick = false;
        private boolean lastCallPlaceShip = false;
        private boolean lastCallBomb = false;

        private String playerName;
        private Player player;
        private Ship ship;
        private BattleshipsPosition battleshipsPosition;
        private boolean horizontal;

        @Override
        public void pickPlayer(String player) throws GameException, StatusException {
            this.lastCallPick = true;
            this.lastCallPlaceShip = false;
            this.lastCallBomb = false;
            this.playerName = player;

        }

        @Override
        public void placeShip(Player player, Ship ship, BattleshipsPosition position, boolean horizontal) throws GameException, StatusException {
            this.lastCallPick = false;
            this.lastCallPlaceShip = true;
            this.lastCallBomb = false;

            this.player = player;
            this.ship = ship;
            this.battleshipsPosition = position;
            this. horizontal = horizontal;
        }

        @Override
        public Outcome bomb(Player player, BattleshipsPosition position) throws GameException, StatusException {
            this.lastCallPick = false;
            this.lastCallPlaceShip = false;
            this.lastCallBomb = true;

            this.player = player;
            this.battleshipsPosition = position;
            return null;
        }

        public boolean isLastCallPick() {
            return lastCallPick;
        }

        public boolean isLastCallPlaceShip() {
            return lastCallPlaceShip;
        }

        public boolean isLastCallBomb() {
            return lastCallBomb;
        }

        public String getPlayerName() {
            return playerName;
        }

        public Player getPlayer() {
            return player;
        }

        public Ship getShip() {
            return ship;
        }

        public BattleshipsPosition getBattleshipsPosition() {
            return battleshipsPosition;
        }

        public boolean isHorizontal() {
            return horizontal;
        }
    }*/



}
