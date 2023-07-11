package ru.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.levachev.Model.Game;

@Component
public class AdministratorDataBaseHandler implements DataBaseHandler{

    private final JdbcTemplate jdbcTemplateGamesDB;
    private final JdbcTemplate jdbcTemplateOrganisationDB;

    @Autowired
    public AdministratorDataBaseHandler(JdbcTemplate jdbcTemplateGamesDB, JdbcTemplate jdbcTemplateOrganisationDB) {
        this.jdbcTemplateGamesDB = jdbcTemplateGamesDB;
        this.jdbcTemplateOrganisationDB=jdbcTemplateOrganisationDB;
    }

    public boolean addGame(Game newGame){
        if(!isNewGameValid(newGame)){
            return false;
        }
        jdbcTemplateGamesDB.update("INSERT INTO Games VALUES(?, ?, ?, ?)",
                newGame.getGameID(), newGame.getGameName(),
                newGame.getGameRules(), newGame.isTaken());
        return true;
    }

    private boolean isNewGameValid(Game newGame){
        Game game = jdbcTemplateGamesDB.query("SELECT * FROM Games WHERE id=?",
                        new Object[]{newGame.getGameID()}, new BeanPropertyRowMapper<>(Game.class)).
                stream().findAny().orElse(null);
        return game == null;
    }

    public void setPassword(int roomNumber, String password){
        jdbcTemplateOrganisationDB.update("UPDATE Room SET password=? WHERE number=?",
                password, roomNumber);
    }

    @Override
    public void truncateTable(String tableName){
        jdbcTemplateGamesDB.execute("TRUNCATE TABLE "+ tableName);
    }
}
