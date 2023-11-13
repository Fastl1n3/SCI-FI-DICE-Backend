package scifidice.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.Entity.Game;
import scifidice.Entity.Room;

@Component
public class AdministratorDataBaseHandler extends DataBaseEntityAdder{
    private final JdbcTemplate jdbcTemplateGamesDB;
    private final JdbcTemplate jdbcTemplateOrganisationDB;

    @Autowired
    public AdministratorDataBaseHandler(JdbcTemplate jdbcTemplateOrganisationDB, JdbcTemplate jdbcTemplateGamesDB) {
        this.jdbcTemplateOrganisationDB=jdbcTemplateOrganisationDB;
        this.jdbcTemplateGamesDB = jdbcTemplateGamesDB;
    }

    public boolean addGame(int id, String name, String rules){
        Game game = new Game(id, name, rules);
        try {
            addGameToTable(game, jdbcTemplateGamesDB);
        } catch (DataAccessException e){
            return false;
        }
        return true;
    }

    public boolean addRoom(int roomNumber, String password, int maxPersonNumber){
        Room room = new Room(roomNumber, password, 0, maxPersonNumber);
        try {
            addRoomToTable(room, jdbcTemplateOrganisationDB);
        } catch (DataAccessException e){
            return false;
        }
        return true;
    }

    public void deleteGame(int gameID){
        jdbcTemplateGamesDB.update("DELETE FROM Games WHERE id=?",
                gameID);
    }

    public void deleteRoom(int roomNumber){
        jdbcTemplateOrganisationDB.update("DELETE FROM Room WHERE number=?",
                roomNumber);
    }

    public void changePassword(int roomNumber, String password){
        jdbcTemplateOrganisationDB.update("UPDATE Room SET password=? WHERE number=?",
                password, roomNumber);
    }
}
