package scifidice.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ResourceUtils;
import scifidice.levachev.Mapper.BufferRoomDataMapper;
import scifidice.levachev.Mapper.GameMapper;
import scifidice.levachev.Mapper.RoomMapper;
import scifidice.levachev.Model.*;

import java.io.IOException;
import java.util.List;

@Component
public class InitializationDBHandler extends DataBaseEntityAdder {
    private final JdbcTemplate jdbcTemplateGamesDB;
    private final JdbcTemplate jdbcTemplateOrganisationDB;

    @Autowired
    public InitializationDBHandler(JdbcTemplate jdbcTemplateOrganisationDB, JdbcTemplate jdbcTemplateGamesDB) {
        this.jdbcTemplateGamesDB = jdbcTemplateGamesDB;
        this.jdbcTemplateOrganisationDB = jdbcTemplateOrganisationDB;
    }

    public void defaultInitialization() throws IOException {
        if (isGameTableEmpty()) {
            initGamesTable();
        }

        if (isRoomTableEmpty()) {
            initRoomTable();
        }

        if (isBufferRoomDataTableEmpty()) {
            initBufferRoomDataTable();
        }
    }

    private boolean isRoomTableEmpty() {
        return jdbcTemplateOrganisationDB.query("SELECT * FROM Room", new RoomMapper()).isEmpty();
    }

    private boolean isBufferRoomDataTableEmpty() {
        return jdbcTemplateOrganisationDB.query("SELECT * FROM bufferRoomData", new BufferRoomDataMapper()).isEmpty();
    }

    private boolean isGameTableEmpty() {
        return jdbcTemplateGamesDB.query("SELECT * FROM Games", new GameMapper()).isEmpty();
    }

    private void initGamesTable() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        GamesResponse gamesResponse = objectMapper.readValue(ResourceUtils.getFile("src/main/resources/config.json"), GamesResponse.class);

        List<Game> gameList = gamesResponse.getGames();
        for (Game game : gameList) {
            addGameToTable(game, jdbcTemplateGamesDB);
        }
    }

    private void initRoomTable() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        RoomsResponse roomsResponse = objectMapper.readValue(ResourceUtils.getFile("src/main/resources/config.json"), RoomsResponse.class);

        List<Room> roomList = roomsResponse.getRooms();

        for (Room room : roomList) {
            addRoomToTable(room, jdbcTemplateOrganisationDB);
        }
    }

    private void initBufferRoomDataTable() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        RoomsResponse roomsResponse = objectMapper.readValue(ResourceUtils.getFile("src/main/resources/config.json"), RoomsResponse.class);

        List<Room> roomList = roomsResponse.getRooms();

        for (Room room : roomList) {
            addRoomDataToTable(
                    new BufferRoomData(room.getNumber()), jdbcTemplateOrganisationDB
            );
        }
    }
}
