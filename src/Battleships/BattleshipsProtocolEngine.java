package Battleships;

import Network.GameSessionEstablishedListener;
import Network.ProtocolEngine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleshipsProtocolEngine implements Battleships, ProtocolEngine, Runnable {
    private OutputStream os;
    private InputStream is;
    private final Battleships gameEngine;
    private String name;

    public static final int METHOD_PICKPLAYER = 0;
    public static final int METHOD_PLACESHIP = 1;
    public static final int METHOD_BOMB = 2;
    private static final int METHOD_OUTCOME = 3;
    private Thread protocolThread = null;
    private Thread pickWaitThread;
    private boolean oracle;
    private String partnerName;


    public BattleshipsProtocolEngine(Battleships gameEngine, String name) {
        this.name = name;
        this.gameEngine = gameEngine;
    }

    //pickplayer no longer needed to avoid unecessary threads
   /* @Override
    public void pickPlayer(String player) throws GameException, StatusException {
        DataOutputStream dos = new DataOutputStream(this.os);

        try {
            //write method id
            dos.writeInt(METHOD_PICKPLAYER);
            //write username
            dos.writeUTF(player);


        } catch (IOException e) {
            throw new GameException("could not serialize command");
        }
    }

    private void deserializePickPlayer() throws GameException {
        DataInputStream dis = new DataInputStream(this.is);

        try {
            //read Username
            String username = dis.readUTF();

            this.gameEngine.pickPlayer(username);
        } catch (IOException | StatusException e) {
            throw new GameException("could not serialize command");
        }

    }*/

    @Override
    public Outcome bomb(int playerInt, BattleshipsPosition position) throws GameException, StatusException, IOException {
        DataOutputStream dos = new DataOutputStream(this.os);

        try {
            //write method id
            dos.writeInt(METHOD_BOMB);
            //write playerInt
            //dos.writeInt(playerInt);
            //write BattleshipspositionObjekt
            dos.writeUTF(position.getxCoordinate());
            dos.writeInt(position.getyCoordinate());

        } catch (IOException e) {
            e.printStackTrace();
            throw new GameException("could not serialize command");
        }


        return null;
    }

    private void deserializeBomb() throws GameException, IOException {
        DataInputStream dis = new DataInputStream(this.is);


        try {
            //int playerInt = dis.readInt();
            String xCoordinate = dis.readUTF();
            int yCoordinate = dis.readInt();

            BattleshipsPosition position = new BattleshipsPosition(xCoordinate, yCoordinate);

            this.gameEngine.bomb(0, position);  //playerInt is always 0 since the local player board is getting bombed by the enemy player

        } catch (IOException | StatusException e) {
            throw new GameException("could not serialize command");
        }

    }

    @Override
    public void placeShip(int playerInt, Ship ship, BattleshipsPosition position, boolean horizontal) throws GameException, StatusException, IOException {
        DataOutputStream dos = new DataOutputStream(this.os);

        int shipInt = ship.getShipSize();

        try {
            //write method id
            dos.writeInt(METHOD_PLACESHIP);
            //write playerInt
            //dos.writeInt(playerInt);          //playerInt is always 1 on the enemy side does not need to be send
            //write ship
            dos.writeInt(shipInt);
            //write position
            dos.writeUTF(position.getxCoordinate());
            dos.writeInt(position.getyCoordinate());
            //write horizontal boolean
            dos.writeBoolean(horizontal);

        } catch (IOException e) {
            throw new GameException("could not serialize command");
        }
    }

    private void deserializePlaceShip() throws GameException, IOException {
        DataInputStream dis = new DataInputStream(this.is);

        try {
            //int playerInt = dis.readInt();
            //read Shipobjekt
            Ship ship = intToShip(dis.readInt());
            //read Battleshipposition
            String xCoordinate = dis.readUTF();
            int yCoordinate = dis.readInt();

            BattleshipsPosition position = new BattleshipsPosition(xCoordinate, yCoordinate);
            //read horizontal boolean
            boolean horizontal = dis.readBoolean();

            this.gameEngine.placeShip(1, ship, position, horizontal); //playerInt is always 1 one enemy side since its not the localPlayer

        } catch (IOException | StatusException | InterruptedException e) {
            throw new GameException("could not serialize command");
        }

    }


    private List<GameSessionEstablishedListener> sessionListenerCreatedList = new ArrayList<>();

//    private void deserializeOutCome() throws GameException {
//        DataInputStream dis = new DataInputStream(this.is);
//        //TODO
//        try {
//            int outComeInt = dis.readInt();
//
//
//        } catch (IOException e) {
//            throw new GameException("could not deserialize command");
//        }
//    }

    @Override
    public void handleConnection(InputStream is, OutputStream os) throws IOException {
        this.is = is;
        this.os = os;

        this.protocolThread = new Thread(this);
        this.protocolThread.start();
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void subscribeGameSessionEstablishedListener(GameSessionEstablishedListener ocListener) {
        this.sessionListenerCreatedList.add(ocListener);
    }

    @Override
    public void unsubscribeGameSessionEstablishedListener(GameSessionEstablishedListener ocListener) {
        this.sessionListenerCreatedList.remove(ocListener);
    }

    public boolean read() throws GameException{
        //this.log("Protocol Engine: read from input stream");
        DataInputStream dis = new DataInputStream((this.is));

        try{
            int commandID = dis.readInt();
            switch (commandID){
                //case METHOD_PICKPLAYER: this.deserializePickPlayer(); break;
                case METHOD_PLACESHIP: this.deserializePlaceShip(); return true;
                case METHOD_BOMB: this.deserializeBomb(); return true;
 //               case METHOD_OUTCOME: this.deserializeOutCome(); return true;
                default: this.log("unkown method id: " + commandID); return false;
            }
        } catch (IOException e) {
            this.log("IOexception caught - most likely connection close - stop thread/ stop engine");
        }
        try {
            this.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Ship intToShip(int shipInt) throws GameException {
        switch (shipInt){
            case 2: return new Ship(ShipModel.SUBMARINE);
            case 3: return new Ship(ShipModel.DESTROYER);
            case 4: return new Ship(ShipModel.BATTLECRUISER);
            case 5: return new Ship(ShipModel.AIRCRAFTCARRIER);
        }
        throw new GameException("Error in deserializing ship");
    }

    @Override
    public void run() {
        this.log("Protocol Engine started - flip a coin");
        long seed = this.hashCode() + System.currentTimeMillis();
        Random random = new Random(seed);

        int localInt = 0, remoteInt = 0;
        try{
            DataOutputStream dos = new DataOutputStream(this.os);
            DataInputStream dis = new DataInputStream(this.is);
            do{
                localInt = random.nextInt();
                this.log("flip and take number " + localInt);
                dos.writeInt(localInt);
                remoteInt = dis.readInt();
            } while (localInt == remoteInt);

            this.oracle = localInt < remoteInt;
            this.log("flipped coint and got an oracle " + this.oracle);

            // exchange names
            dos.writeUTF(this.name);
            this.partnerName = dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // call listener
        if(this.sessionListenerCreatedList != null && !this.sessionListenerCreatedList.isEmpty()){
            for(GameSessionEstablishedListener oclistener : this.sessionListenerCreatedList){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1); //let thread read
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Listener established");
                        oclistener.gameSessionEstablished(
                                BattleshipsProtocolEngine.this.oracle,
                                BattleshipsProtocolEngine.this.partnerName);
                    }
                }).start();
            }
        }

        try{
            boolean again = true;
            while(again){
                again = this.read();
            }
        } catch (GameException e) {
            System.out.println("exception called in protocol engine thread - fatal and stop");;
            e.printStackTrace();
            //leave while - end thread
        }


    }

    private String produceLogString(String message) {
        StringBuilder sb = new StringBuilder();
        if(this.name != null) {
            sb.append(this.name);
            sb.append(": ");
        }

        sb.append(message);

        return sb.toString();
    }


    private void log(String message) {
        System.out.println(this.produceLogString(message));
    }

    private void logError(String message) {
        System.err.println(this.produceLogString(message));
    }
}

