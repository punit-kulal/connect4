package game.conenct4.api;

import game.conenct4.entities.Connect4;
import game.conenct4.entities.Connect4Request;
import game.conenct4.entities.Player;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.tags.EscapeBodyTag;

import java.util.Map;

@RestController
@RequestMapping("/")
public class GameController {

    private Connect4 singleGame;
    private static final String GAME_OVER = "gameOver";
    private static final String VALIDITY ="validity";

    public GameController(){
        this.singleGame = new Connect4();
    }

    @PostMapping("connect4")
    public Map<String, Object> connect4(@RequestBody
    Connect4Request connect4Request){
        if (connect4Request.isStartRequest()){
            singleGame = new Connect4();
            return Map.of(
                    "ready", true
            );
        }else {
            boolean valid = singleGame.makeMove(connect4Request.getColumn());
            if (valid){
                boolean gameWin = singleGame.didWin();
                if (gameWin){
                    Player p = singleGame.getTurn().getNext();
                    singleGame = null; //finish game
                    return Map.of(
                            GAME_OVER, true,
                            VALIDITY, true,
                            "win", p
                    );
                }else{
                    return Map.of(
                            GAME_OVER, false,
                            VALIDITY, true,
                            "nextMove", singleGame.getTurn()
                    );
                }
            }else{
                return Map.of(
                        GAME_OVER, !singleGame.moveLeft(),
                        VALIDITY, false,
                        "nextMove", singleGame.getTurn()
                );
            }
        }
    }
}
