package Battleships;



public class BattleshipsPosition {
    private final String xCoordinate;
    private final int yCoordinate;
    PositionStatus status = PositionStatus.NOTHING;

    public BattleshipsPosition(String xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        status = PositionStatus.NOTHING;
    }

    public String getxCoordinate() {
        return xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public void setStatus(PositionStatus status) {
        this.status = status;
    }

    public PositionStatus getStatus() {
        return this.status;
    }
}
