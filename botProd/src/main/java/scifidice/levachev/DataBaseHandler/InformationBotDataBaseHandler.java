package scifidice.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.levachev.Mapper.GameMapper;
import scifidice.levachev.Model.Game;

@Component
public class InformationBotDataBaseHandler {
    private final JdbcTemplate jdbcTemplateGamesDB;
    private final JdbcTemplate jdbcTemplateOrganisationDB;

    @Autowired
    public InformationBotDataBaseHandler(JdbcTemplate jdbcTemplateOrganisationDB, JdbcTemplate jdbcTemplateGamesDB) {
        this.jdbcTemplateOrganisationDB=jdbcTemplateOrganisationDB;
        this.jdbcTemplateGamesDB = jdbcTemplateGamesDB;
    }

    public boolean authorization(String phoneNumber, String infoBotChatID){
        updatePerson(phoneNumber, infoBotChatID);
        return true;
    }

    public String getRules(int gameID){
        Game game = jdbcTemplateGamesDB.query("SELECT * FROM Games WHERE id=?",
                        new Object[]{gameID}, new GameMapper())
                .stream().findAny().orElse(null);
        if(game == null){
            return null;
        } else{
            return game.getRules();
        }
    }

    private void updatePerson(String phoneNumber, String infoBotChatID){
        jdbcTemplateOrganisationDB.update("UPDATE Person SET infobotchatid=? WHERE phoneNumber=?",
                infoBotChatID, phoneNumber);
    }
}
