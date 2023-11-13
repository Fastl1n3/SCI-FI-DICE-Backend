package scifidice.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.db.entities.Game;

@Component
public class GameDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GameDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    void save(Game game) {
        jdbcTemplate.update("INSERT INTO Game VALUES(?, ?, ?, ?)",
                game.getGameId(), game.getName(),
                game.getRules(), game.isTaken());
    }

    public void deleteById(int gameID) {
        jdbcTemplate.update("DELETE FROM Game WHERE id=?",
                gameID);
    }
}
