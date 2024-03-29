package scifidice.db.dataBaseHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import scifidice.model.GamesList;
import scifidice.model.RoomsList;
import scifidice.db.dao.GameDao;
import scifidice.db.dao.RoomDao;
import scifidice.db.entities.Game;
import scifidice.db.entities.Room;

import java.io.IOException;
import java.util.List;

@Component
public class InitializationDBHandler {

    private final RoomDao roomDao;
    private final GameDao gameDao;

    public InitializationDBHandler(RoomDao roomDao, GameDao gameDao) {
        this.roomDao = roomDao;
        this.gameDao = gameDao;
    }

    public void defaultInitialization() throws IOException {
        if (gameDao.checkEmpty()) {
            initGamesTable();
        }

        if (roomDao.checkEmpty()) {
            initRoomTable();
        }
    }

    private void initGamesTable() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        GamesList games = objectMapper.readValue(ResourceUtils.getFile("src/main/resources/config.json"), GamesList.class);

        List<Game> gameList = games.getGames();
        for (Game game : gameList) {
            gameDao.add(game);
        }
    }

    private void initRoomTable() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        RoomsList rooms = objectMapper.readValue(ResourceUtils.getFile("src/main/resources/config.json"), RoomsList.class);

        List<Room> roomList = rooms.getRooms();

        for (Room room : roomList) {
            roomDao.add(room);
        }
    }

}
