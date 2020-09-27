package game.conenct4.entities;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
public class Connect4 {

    public static final int COLUMN_SIZE = 7;
    public static final int ROW_SIZE = 6;
    Player[][] board;
    private Player turn;

    public Connect4(){
        board = new Player[COLUMN_SIZE][ROW_SIZE];
        turn = Player.YELLOW;
    }

    public boolean makeMove(int column){
        for (int i = 0; i < 6; i++) {
            if (board[column][i] == null){
                board[column][i] = turn;
                turn = turn.getNext();
                return true;
            }
        }
        return false;

    }

    public boolean didWin(){
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] != null){
                    boolean value = checkDiagonal(i, j) || checkRow(i, j) || checkColumn(i, j);
                    if (value)
                        return true;
                }
            }
        }
        return false;
    }

    private boolean checkColumn(int i, int j) {
        Player original = board[i][j];
        int count = 1;
        for (int k = i+1; k < COLUMN_SIZE; k++) {
            if (original.equals(board[k][j])){
                count++;
                if (count == 4)
                    return true;
            }else {
                return false;
            }
        }
        return false;
    }

    private boolean checkRow(int i, int j) {
        Player original = board[i][j];
        int count = 1;
        for (int k = j+1; k < ROW_SIZE; k++) {
            if (original.equals(board[i][k])){
                count++;
                if (count == 4)
                    return true;
            }else {
                return false;
            }
        }
        return false;
    }

    private boolean checkDiagonal(int i, int j) {
        Player original = board[i][j];
        int count = 1;
        int temp1 = i+1;
        int temp2 = j+1;
        while(temp1 < COLUMN_SIZE && temp2 < ROW_SIZE){
            if (original.equals(board[temp1][temp2])){
                count++;
                temp1++;
                temp2++;
                if (count == 4)
                    return true;
            }else {
                break;
            }
        }

        count = 1;
        temp1 = i + 1;
        temp2 = j - 1;
        while(temp1 < COLUMN_SIZE && temp2 >= 0){
            if (original.equals(board[temp1][temp2])){
                count++;
                temp1++;
                temp2--;
                if (count == 4)
                    return true;
            }else {
                break;
            }
        }

        count = 1;
        temp1 = i - 1;
        temp2 = j - 1;
        while(temp1 >= 0 && temp2 >= 0){
            if (original.equals(board[temp1][temp2])){
                count++;
                temp1--;
                temp2--;
                if (count == 4)
                    return true;
            }else {
                break;
            }
        }

        count = 1;
        temp1 = i - 1;
        temp2 = j + 1;
        while(temp1 >= 0 && temp2 < ROW_SIZE){
            if (original.equals(board[temp1][temp2])){
                count++;
                temp1--;
                temp2++;
                if (count == 4)
                    return true;
            }else {
                break;
            }
        }

        return false;
    }

    public boolean moveLeft() {
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COLUMN_SIZE; j++) {
                if (board[i][j] == null)
                    return true;
            }
        }
        return  false;
    }
}
