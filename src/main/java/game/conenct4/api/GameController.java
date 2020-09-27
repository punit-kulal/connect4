package game.conenct4.api;

import game.conenct4.entities.Connect4;
import game.conenct4.entities.Connect4Request;
import game.conenct4.entities.Move;
import game.conenct4.entities.ParallelConnect4Request;
import game.conenct4.entities.Player;
import game.conenct4.repository.MoveRepository;
import org.apache.commons.collections4.map.LRUMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.tags.EscapeBodyTag;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class GameController {

    private Connect4 singleGame;
    private static final String GAME_OVER = "gameOver";
    private static final String VALIDITY = "validity";
    private static final int maxParallelGames = 100;
    private Map<String, Connect4> parallelGames;
    private MoveRepository moveRepository;

    public GameController(MoveRepository moveRepository) {
        this.moveRepository = moveRepository;
        this.singleGame = new Connect4();
        this.parallelGames = Collections.synchronizedMap(new LRUMap<>(maxParallelGames));
    }

    @PostMapping("connect4")
    public Map<String, Object> connect4(@RequestBody
                                                Connect4Request connect4Request) {
        if (connect4Request.isStartRequest()) {
            singleGame = new Connect4();
            return Map.of(
                    "ready", true
            );
        } else {
            boolean valid = singleGame.makeMove(connect4Request.getColumn());
            if (valid) {
                boolean gameWin = singleGame.didWin();
                if (gameWin) {
                    Player p = singleGame.getTurn().getNext();
                    singleGame = null; //finish game
                    return Map.of(
                            GAME_OVER, true,
                            VALIDITY, true,
                            "win", p
                    );
                } else {
                    return Map.of(
                            GAME_OVER, false,
                            VALIDITY, true,
                            "nextMove", singleGame.getTurn()
                    );
                }
            } else {
                return Map.of(
                        GAME_OVER, !singleGame.moveLeft(),
                        VALIDITY, false,
                        "nextMove", singleGame.getTurn()
                );
            }
        }
    }

    @PostMapping("parallel/connect4")
    public Map<String, Object> parallelConnect4(@RequestBody ParallelConnect4Request connect4Request) {
        if (connect4Request.isStartRequest()) {
            Connect4 game = new Connect4();
            String token = UUID.randomUUID().toString();
            parallelGames.put(token, game);
            return Map.of(
                    "ready", true,
                    "token", token
            );
        } else {
            String token = connect4Request.getToken();
            Connect4 game = parallelGames.get(token);
            if ("".equals(token) || game == null) {
                throw new InputMismatchException("Incorrect token");
            }
            boolean valid = game.makeMove(connect4Request.getColumn());
            if (valid) {
                Move move = new Move(token, connect4Request.getColumn());
                moveRepository.save(move);
                boolean gameWin = game.didWin();
                if (gameWin) {
                    Player p = game.getTurn().getNext();
                    parallelGames.remove(token); //finish game
                    return Map.of(
                            GAME_OVER, true,
                            VALIDITY, true,
                            "win", p
                    );
                } else {
                    return Map.of(
                            GAME_OVER, false,
                            VALIDITY, true,
                            "nextMove", game.getTurn()
                    );
                }
            } else {
                boolean gameLeft = game.moveLeft();
                if (!gameLeft) {
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

    @GetMapping("/parallel/moves/{{token}}")
    public Map<String, Object> getMoves(@PathVariable("{token}") String token) {
        List<Move> moves = moveRepository.findByToken(token);
        return Map.of(
                "token", token,
                "moves", moves.stream().sorted(Comparator.comparingLong(Move::getTimeStamp)).collect(Collectors.toList())
        );

    }
}
