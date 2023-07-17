package ru.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ResourceUtils;
import ru.levachev.Model.*;

import java.io.IOException;
import java.util.List;

@Component
public class InitializationDBHandler extends DataBaseEntityAdder{
    private final JdbcTemplate jdbcTemplateGamesDB;
    private final JdbcTemplate jdbcTemplateOrganisationDB;

    @Autowired
    public InitializationDBHandler(JdbcTemplate jdbcTemplateOrganisationDB, JdbcTemplate jdbcTemplateGamesDB) {
        this.jdbcTemplateGamesDB = jdbcTemplateGamesDB;
        this.jdbcTemplateOrganisationDB=jdbcTemplateOrganisationDB;
    }

    public void defaultInitialization() throws IOException {
        initGamesTable();
        initRoomTable();
    }

    public void initGamesTable() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        GamesResponse gamesResponse= objectMapper.readValue(ResourceUtils.getFile("classpath:config.json"), GamesResponse.class);

        List<Game> gameList = gamesResponse.getGames();
        for (Game game : gameList){
            addGameToTable(game, jdbcTemplateGamesDB);
        }
    }

    public void initRoomTable() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        RoomsResponse roomsResponse= objectMapper.readValue(ResourceUtils.getFile("/home/rlevachev/DBBack/src/main/resources/config.json"), RoomsResponse.class);

        List<Room> roomList = roomsResponse.getRooms();

        for (Room room : roomList){
            addRoomToTable(room, jdbcTemplateOrganisationDB);
            addRoomDataToTable(
                    new BufferRoomData(room.getNumber()), jdbcTemplateOrganisationDB
            );
        }
    }
}
