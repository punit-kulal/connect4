package game.conenct4.repository;

import game.conenct4.entities.Move;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public
interface MoveRepository extends CrudRepository<Move, Long> {

    public List<Move> findByToken(String move);
}
