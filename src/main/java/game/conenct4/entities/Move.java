package game.conenct4.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.time.Instant;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name="move_log")
public class Move {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token")
    String token;

    @Column(name = "column")
    int move;

    @Column(name = "timestamp")
    long timeStamp;

    public Move(String token, int column) {
        this.token = token;
        this.move = column;
        this.timeStamp = System.currentTimeMillis();
    }

    public Move(){}
}
