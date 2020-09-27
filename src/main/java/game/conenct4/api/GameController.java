package game.conenct4.api;

import game.conenct4.entities.Connect4;
import game.conenct4.entities.Connect4Request;
import game.conenct4.entities.ParallelConnect4Request;
import game.conenct4.entities.Player;
import org.apache.commons.collections4.map.LRUMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.tags.EscapeBodyTag;

import java.util.Collections;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/")
public class GameController {

    private Connect4 singleGame;
    private static final String GAME_OVER = "gameOver";
    private static final String VALIDITY ="validity";
    private static final int maxParallelGames = 100;
    private Map<String, Connect4> parallelGames;

    public GameController(){
        this.singleGame = new Connect4();
        this.parallelGames = Collections.synchronizedMap(new LRUMap<>(maxParallelGames));
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

    @PostMapping("parallel/connect4")
    public Map<String, Object> parallelConnect4(@RequestBody ParallelConnect4Request connect4Request){
        if (connect4Request.isStartRequest()){
            Connect4 game = new Connect4();
            String token = UUID.randomUUID().toString();
            parallelGames.put(token, game);
            return Map.of(
                    "ready", true,
                    "token", token
            );
        }else {
            String token = connect4Request.getToken();
            Connect4 game = parallelGames.get(token);
            if ("".equals(token) || game == null){
                throw new InputMismatchException("Incorrect token");
            }
            boolean valid = game.makeMove(connect4Request.getColumn());
            if (valid){
                boolean gameWin = game.didWin();
                if (gameWin){
                    Player p = game.getTurn().getNext();
                    parallelGames.remove(token); //finish game
                    return Map.of(
                            GAME_OVER, true,
                            VALIDITY, true,
                            "win", p
                    );
                }else{
                    return Map.of(
                            GAME_OVER, false,
                            VALIDITY, true,
                            "nextMove", game.getTurn()
                    );
                }
            }else{
                boolean gameLeft = game.moveLeft();
                if (!gameLeft){
                    parallelGames.remove(token);
                }
                return Map.of(
                        GAME_OVER, !gameLeft,
                        VALIDITY, false,
                        "nextMove", game.getTurn()
                );
            }
        }
    }
}
