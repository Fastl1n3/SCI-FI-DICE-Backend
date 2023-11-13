package scifidice.db.dataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.db.mapper.GameMapper;
import scifidice.db.entities.Game;

import java.util.List;

@Component
public class InformationBotDataBaseHandler {
    private final JdbcTemplate jdbcTemplateGamesDB;
    private final JdbcTemplate jdbcTemplateOrganisationDB;

    @Autowired
    public InformationBotDataBaseHandler(JdbcTemplate jdbcTemplateOrganisationDB, JdbcTemplate jdbcTemplateGamesDB) {
        this.jdbcTemplateOrganisationDB = jdbcTemplateOrganisationDB;
        this.jdbcTemplateGamesDB = jdbcTemplateGamesDB;
    }

    public int authorization(String phoneNumber, String infoBotChatID) {
        return updatePerson(phoneNumber, infoBotChatID);
    }

    public String getRules(int gameID) {
        Game game = jdbcTemplateGamesDB.query("SELECT * FROM Games WHERE id=?",
                        new Object[]{gameID}, new GameMapper())
                .stream().findAny().orElse(null);
        if (game == null) {
            return null;
        } else {
            return game.getRules();
        }
    }

    public List<Game> getGames() {
        return jdbcTemplateGamesDB.query("SELECT * FROM games ORDER BY id", new Object[]{}, new GameMapper());
    }

    private int updatePerson(String phoneNumber, String infoBotChatID) {
        return jdbcTemplateOrganisationDB.update("UPDATE Person SET infobotchatid=? WHERE phoneNumber=?",
                infoBotChatID, phoneNumber);
    }
}
