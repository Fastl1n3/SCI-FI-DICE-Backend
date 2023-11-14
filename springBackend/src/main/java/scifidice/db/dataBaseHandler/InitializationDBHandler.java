package scifidice.db.dataBaseHandler;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ResourceUtils;
import scifidice.db.dao.GameDao;
import scifidice.db.dao.RoomDao;
import scifidice.db.entities.Game;
import scifidice.db.entities.Room;
import scifidice.db.mapper.GameMapper;
import scifidice.db.mapper.RoomMapper;

import java.io.IOException;
import java.util.List;

@Component
public class InitializationDBHandler extends DataBaseEntityAdder {

private final RoomDao roomDao;
private final GameDao gameDao;

    public InitializationDBHandler(RoomDao roomDao, GameDao gameDao) {
        this.roomDao = roomDao;
        this.gameDao = gameDao;
    }

    public void defaultInitialization() throws IOException {
        if (isGameTableEmpty()) {
            initGamesTable();
        }

        if (isRoomTableEmpty()) {
            initRoomTable();
        }
    }

    private boolean isRoomTableEmpty() {
        return roomDao.getAllRooms().isEmpty();
    }

    private boolean isGameTableEmpty() {
        return gameDao.getAllGames().isEmpty();
    }

    private void initGamesTable() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        GamesResponse gamesResponse = objectMapper.readValue(ResourceUtils.getFile("src/main/resources/config.json"), GamesResponse.class);

        List<Game> gameList = gamesResponse.getGames();
        for (Game game : gameList) {
            gameDao.add(game);
        }
    }

    private void initRoomTable() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        RoomsResponse roomsResponse = objectMapper.readValue(ResourceUtils.getFile("src/main/resources/config.json"), RoomsResponse.class);

        List<Room> roomList = roomsResponse.getRooms();

        for (Room room : roomList) {
            roomDao.add(room);
        }
    }

   /* private void initBufferRoomDataTable() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        RoomsResponse roomsResponse = objectMapper.readValue(ResourceUtils.getFile("src/main/resources/config.json"), RoomsResponse.class);

        List<Room> roomList = roomsResponse.getRooms();

        for (Room room : roomList) {
            addRoomDataToTable(
                    new BufferRoomData(room.getNumber()), jdbcTemplateOrganisationDB
            );
        }
    }*/
}
