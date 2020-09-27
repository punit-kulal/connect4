package game.conenct4.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.InputMismatchException;

@Data
public class ParallelConnect4Request {

    @JsonProperty(value = "token", defaultValue = "")
    String token;

    @JsonProperty(value = "isStart")
    boolean isStartRequest = false;

    @JsonProperty(value = "column")
    int column = -1;

    @JsonSetter(value = "isStart")
    public void setIsStart(String isStart){
        if (isStart != null){
            this.isStartRequest = true;
        }
    }

    @JsonSetter(value = "column")
    public void setColumn(int columnIndex){
        if (columnIndex < 0 || columnIndex >= Connect4.COLUMN_SIZE){
            throw new InputMismatchException("Column value should be between 0 and 6");
        }
        this.column = columnIndex;
    }
}
