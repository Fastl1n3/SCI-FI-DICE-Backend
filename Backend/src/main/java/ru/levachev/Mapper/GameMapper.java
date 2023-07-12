package ru.levachev.Mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.levachev.Model.Game;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GameMapper implements RowMapper<Game> {

    @Override
    public Game mapRow(ResultSet resultSet, int i) throws SQLException {
        Game game = new Game();

        game.setId(resultSet.getInt("id"));
        game.setName(resultSet.getString("name"));
        game.setRules(resultSet.getString("rules"));
        game.setTaken(resultSet.getBoolean("isTaken"));

        return game;
    }
}
