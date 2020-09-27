package game.conenct4.entities;

public enum Player {
    YELLOW,
    RED;

    public Player getNext(){
        return this.equals(YELLOW)?RED:YELLOW;
    }
}
