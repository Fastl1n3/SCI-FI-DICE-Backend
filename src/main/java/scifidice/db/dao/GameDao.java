package scifidice.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.db.entities.Game;
import scifidice.db.mapper.GameMapper;

import java.util.List;

@Component
public class GameDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GameDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Game getGameByGameId(int gameId) {
        return jdbcTemplate.query("SELECT * FROM Game WHERE game_id=?",
                        new Object[]{gameId}, new GameMapper())
                .stream().findAny().orElse(null);
    }

    public boolean checkEmpty() {
        return jdbcTemplate.query("SELECT * FROM Game", new Object[]{}, new GameMapper()).isEmpty();
    }

    public List<Game> getAllGames() {
        return jdbcTemplate.query("SELECT * FROM Game ORDER BY game_id", new Object[]{}, new GameMapper());
    }

    public void add(Game game) {
        jdbcTemplate.update("INSERT INTO Game VALUES(?, ?, ?, ?)",
                game.getGameId(), game.getName(),
                game.getRules(), game.isTaken());
    }

    public void updateIsTakenByGameId(int gameId, boolean isTaken) {
        jdbcTemplate.update("UPDATE Game SET is_taken=? WHERE id=?",
                isTaken, gameId);
    }

    public void deleteGameById(int gameID) {
        jdbcTemplate.update("DELETE FROM Game WHERE game_id=?",
                gameID);
    }
}
