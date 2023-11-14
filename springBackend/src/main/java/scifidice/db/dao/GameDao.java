package scifidice.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.db.entities.Game;

import java.util.List;

@Component
public class GameDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GameDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void add(Game game) {
        jdbcTemplate.update("INSERT INTO Game VALUES(?, ?, ?, ?)",
                game.getGameId(), game.getName(),
                game.getRules(), game.isTaken());
    }

    public void updateIsTakenByGame(Game game) {
        jdbcTemplate.update("UPDATE Games SET is_taken=? WHERE id=?",
                false, game.getGameId());
    }

    public void deleteGameById(int gameID) {
        jdbcTemplate.update("DELETE FROM Game WHERE game_id=?",
                gameID);
    }
}
