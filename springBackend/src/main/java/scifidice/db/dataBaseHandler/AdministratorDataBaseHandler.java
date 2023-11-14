package scifidice.db.dataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.db.dao.GameDao;
import scifidice.db.dao.RoomDao;
import scifidice.db.entities.Game;
import scifidice.db.entities.Room;

@Component
public class AdministratorDataBaseHandler {

    private final GameDao gameDao;

    private final RoomDao roomDao;

    @Autowired
    public AdministratorDataBaseHandler(GameDao gameDao, RoomDao roomDao) {
        this.gameDao = gameDao;
        this.roomDao = roomDao;
    }

    public boolean addGame(String name, String rules){
        Game game = new Game(name, rules);
        try {
            gameDao.add(game);
        } catch (DataAccessException e){
            return false;
        }
        return true;
    }

    public boolean addRoom(int roomNumber, String password, int maxPersonNumber){
        Room room = new Room(roomNumber, password, maxPersonNumber);
        try {
            roomDao.add(room);
        } catch (DataAccessException e){
            return false;
        }
        return true;
    }

}
