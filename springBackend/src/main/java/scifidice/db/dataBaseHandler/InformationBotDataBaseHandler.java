package scifidice.db.dataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.db.dao.GameDao;
import scifidice.db.dao.PersonDao;
import scifidice.db.entities.Game;

import java.util.List;

@Component
public class InformationBotDataBaseHandler {

    private final GameDao gameDao;

    private final PersonDao personDao;

    @Autowired
    public InformationBotDataBaseHandler(GameDao gameDao, PersonDao personDao) {
        this.gameDao = gameDao;
        this.personDao = personDao;
    }

    public int authorization(String phoneNumber, String infoBotChatID) {
        return updatePerson(phoneNumber, infoBotChatID);
    }

    public String getRules(int gameID) {
        Game game = gameDao.getGameByGameId(gameID);
        if (game == null) {
            return null;
        } else {
            return game.getRules();
        }
    }

    public List<Game> getGames() {
        return gameDao.getAllGames();
    }

    private int updatePerson(String phoneNumber, String infoBotChatID) {
        return personDao.setInfoBotChatIdByPhone(phoneNumber, infoBotChatID);
    }
}
