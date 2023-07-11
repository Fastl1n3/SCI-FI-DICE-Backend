package ru.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.levachev.Mapper.GameMapper;
import ru.levachev.Model.Game;

@Component
public class InformationBotDataBaseHandler implements DataBaseHandler{
    private final JdbcTemplate jdbcTemplateGamesDB;

    @Autowired
    public InformationBotDataBaseHandler(JdbcTemplate jdbcTemplateGamesDB){
        this.jdbcTemplateGamesDB=jdbcTemplateGamesDB;
    }

    public String getRules(int gameID){
        Game game = jdbcTemplateGamesDB.query("SELECT * FROM Games WHERE id=?",
                        new Object[]{gameID}, new GameMapper())
                .stream().findAny().orElse(null);
        if(game == null){
            return null;
        } else{
            return game.getGameRules();
        }
    }

    @Override
    public void truncateTable(String tableName){
        jdbcTemplateGamesDB.execute("TRUNCATE TABLE "+ tableName);
    }

}
