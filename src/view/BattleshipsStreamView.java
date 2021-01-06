package view;

import Battleships.BattleshipsPosition;
import Battleships.Ship;

import java.io.PrintStream;

public class BattleshipsStreamView implements PrintStreamView {
    private static final int PLAYERINT = 0;
    private final Ship[][] ocean;
    private final BattleshipsPosition[][] hitOcean;

    public BattleshipsStreamView(Ship[][] ocean, BattleshipsPosition[][] hitOcean) {
        this.ocean = ocean;
        this.hitOcean = hitOcean;
    }

    @Override
    public void print(PrintStream ps) {
        for(int v = 9; v > -1; v--){
            ps.print(v + " ");
            for(int h = 0; h < 9; h++){
                Ship ship = this.ocean[h][v];
                if(ship == null) {
                    System.out.println(" ");
                    } else {
                    System.out.println(" S " );
                }
            }
        }
    }
    public void printHits(PrintStream ps){
        for(int v = 9; v > -1; v--){
            ps.print(v + " ");
            for(int h = 0; h < 9; h++){
                BattleshipsPosition position = this.hitOcean[h][v];
                if(position == null) {
                    System.out.println(" O ");
                } else {
                    System.out.println(" X ");
                }
            }
        }
    }
}
