package UI;

import Battleships.*;
import Network.GameSessionEstablishedListener;
import Network.TCPStream;
import Network.TCPStreamCreatedListener;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class BattleShipsUI implements LocalBoardChangeListener, TCPStreamCreatedListener, GameSessionEstablishedListener {
    private final String OPEN = "open";
    private final String CONNECT = "connect";
    private final String PRINT = "print";
    private final String PRINTHITS = "printHits";
    private final String PLACESHIP = "place";
    private final String BOMB = "bomb";
    private final String EXIT = "exit";
    private final String SHOWNEXTSHIP = "shownext";
    private final int DEFAULT_PORT = 696969;

    private final String playerName;
    private String partnerName;
    private final PrintStream outStream;
    private final BufferedReader inBufferedReader;
    private BattleShipsImpl gameEngine = null;
    private BattleshipsProtocolEngine protocolEngine;
    private BattleShipsLocalBoard localBoard;
    private TCPStream tcpStream;
    private int fleetIndex = 0;

    public static void main(String[] args) throws GameException {
        System.out.println("Welcome to Battleships");

        if (args.length < 1) {
            System.out.println("need a player name as parameter");
            System.exit(1);
        }

        System.out.println("Welcome" + args[0]);

        BattleShipsUI userCMD = new BattleShipsUI(args[0], System.out, System.in);

        userCMD.printUsage();
        userCMD.runCommandLoop();
    }

    public BattleShipsUI(String playerName, PrintStream os, InputStream is) throws GameException {
        this.playerName = playerName;
        this.outStream = os;
        this.inBufferedReader = new BufferedReader(new InputStreamReader(is));

        this.gameEngine = new BattleShipsImpl(playerName);
        this.localBoard = this.gameEngine;
        this.localBoard.subscribeChangeListener(this);
    }

    private final Ship[] fleet = this.gameEngine.buildShips();

    private void printUsage() {
        StringBuilder b = new StringBuilder();

        b.append("\n");
        b.append("\n");
        b.append("valid commands:");
        b.append("\n");
        b.append(CONNECT);
        b.append(".. connect as tcp client");
        b.append("\n");
        b.append(OPEN);
        b.append(".. open port become tcp server");
        b.append("\n");
        b.append(PRINT);
        b.append(".. print board");
        b.append("\n");
        b.append(PRINTHITS);
        b.append(".. print your previous hits");
        b.append("\n");
        b.append(SHOWNEXTSHIP);
        b.append(".. Show me the next Ship");
        b.append("\n");
        b.append(PLACESHIP);
        b.append("place a ship");
        b.append("\n");
        b.append(BOMB);
        b.append("bomb your enemy!");
        b.append("\n");
        b.append(EXIT);
        b.append(".. exit");

        this.outStream.println(b.toString());
    }

    private void runCommandLoop() {
        boolean again = true;

        while(again) {
            boolean rememberCommand = true;
            String cmdLineString = null;

            try{
                //read userinput
                cmdLineString = inBufferedReader.readLine();

                //exit if no command es been given
                if(cmdLineString == null) break;

                //trim whitespace from command
                cmdLineString = cmdLineString.trim();

                //extract command
                int spaceIndex = cmdLineString.indexOf(' ');
                spaceIndex = spaceIndex != -1 ? spaceIndex : cmdLineString.length();

                //got command string
                String commandString = cmdLineString.substring(0, spaceIndex);

                //get parameters can be empty
                String parameterString = cmdLineString.substring(spaceIndex);
                parameterString = parameterString.trim();

                //start command loop
                switch (commandString) {
                    case PRINT: this.doPrint(); break;
                    case PRINTHITS : this.doPrintHits(); break;
                    case CONNECT : this.doConnect(parameterString); break;
                    case OPEN : this.doOpen(); break;
                    case SHOWNEXTSHIP : this.doShowNextShip(); break;
                    case PLACESHIP : this.doPlaceShip(parameterString); this.doPrint(); break;  //show new ocean after placing
                    case BOMB : this.doBomb(parameterString); this.doPrint(); break;
                    case "q": //convenience (?)
                    case EXIT:
                        again = false; this.doExit(); break; //endloop
                }
            } catch (IOException ex) {
                this.outStream.println("cannt read from input - fatal, prepare to die");
                try {
                    this.doExit();
                } catch (IOException e) {
                    //ignore
                }
            } catch (StatusException ex) {
                this.outStream.println("wrong status: " + ex.getLocalizedMessage());
            }
            catch (GameException ex){
                this.outStream.println("game exception: " + ex.getLocalizedMessage());
            }
            catch (RuntimeException ex){
                this.outStream.println("runtime Problem: " + ex.getLocalizedMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                           ui method implementations                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void doOpen() {
        if(this.alreadyConnected()) return;

        this.tcpStream = new TCPStream(DEFAULT_PORT, true , this.playerName);
        this.tcpStream.setStreamCreationListener(this);
        this.tcpStream.start();
    }
    private void doConnect(String parameterString) {
        if(this.alreadyConnected()) return;

        String hostname = null;

        try{
            StringTokenizer st = new StringTokenizer(parameterString);
            hostname = st.nextToken();
        } catch (NoSuchElementException e){
            System.out.println("no hostname provided - take localhost");
            hostname = "localhost";
        }

        this.tcpStream = new TCPStream(DEFAULT_PORT, false, this.playerName);
        this.tcpStream.setRemoteEngine(hostname);
        this.tcpStream.setStreamCreationListener(this);
        this.tcpStream.start();
    }

    private void doShowNextShip() {
        System.out.println("next ship to place is: " + fleet[fleetIndex].getType());
    }

    private void doPlaceShip(String parameterString) throws StatusException, InterruptedException, IOException, GameException {
        this.checkConnectionStatus(); // call guard

        StringTokenizer st = new StringTokenizer(parameterString);
        String xCoordinate = st.nextToken();
        int yCoordinate = Integer.parseInt(st.nextToken());
        boolean horizontal = Boolean.parseBoolean(st.nextToken());
        BattleshipsPosition position = new BattleshipsPosition(xCoordinate,yCoordinate);

        this.gameEngine.placeShip(fleet[fleetIndex], position, horizontal);
        fleetIndex++;           // walks along fleet array to determine which ship to place, players cannot choose
    }

    private void doBomb(String parameterString) throws StatusException, IOException, GameException {
        this.checkConnectionStatus(); // call guard

        StringTokenizer st = new StringTokenizer(parameterString);
        String xCoordinate = st.nextToken();
        int yCoordinate = Integer.parseInt(st.nextToken());
        BattleshipsPosition position = new BattleshipsPosition(xCoordinate,yCoordinate);
        this.gameEngine.bomb(position);
    }

    private void checkConnectionStatus() throws StatusException {  //check of protocol engine has already been created
        if(this.protocolEngine == null){
            throw new StatusException("not yet connected - call connect or open");
        }
    }

    private void doExit() throws IOException {
        //shutdown engines
        this.protocolEngine.close();
    }
    private boolean alreadyConnected(){
        if(this.tcpStream != null){
            System.err.println("connection already established or connection attempt in progress");
            return true;
        }
        return false;
    }

    @Override
    public void changed() {
        try {
            this.doPrint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doPrintHits() {
        this.gameEngine.getPrintStreamView().printHits(System.out);
    }

    private void doPrint() {
        this.gameEngine.getPrintStreamView().print(System.out);
    }

    @Override
    public void streamCreated(TCPStream channel) {
        //connection establed, create protocolEngine
        System.out.println("stream created - setuo engine - we shall bomb each other in no time");
        this.protocolEngine = new BattleshipsProtocolEngine(this.gameEngine, this.playerName);
        this.gameEngine.setProtocolEngine(protocolEngine);

        this.protocolEngine.subscribeGameSessionEstablishedListener(this);

        try{
            protocolEngine.handleConnection(tcpStream.getInputStream(), tcpStream.getOutputStream());
        } catch (IOException e) {
            System.err.println("cannot get stream from tcpStream - fatal, prepare to die" + e.getLocalizedMessage());
            System.exit(1);
        }
    }

    @Override
    public void gameSessionEstablished(boolean oracle, String partnerName) {
        System.out.println("game session created");
        this.partnerName = partnerName;

        if(oracle){
            System.out.println("you go first after placing ships");
        } else {
            System.out.println("partner goes first after placing ships");
        }
    }
}
