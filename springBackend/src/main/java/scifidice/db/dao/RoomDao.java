package scifidice.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.db.entities.Room;

@Component
public class RoomDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RoomDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public void changePassword(int roomNumber, String password){
        jdbcTemplate.update("UPDATE Room SET password=? WHERE number=?",
                password, roomNumber);
    }
    void save(Room room, JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("INSERT INTO Room VALUES(?, ?, ?)",
                room.getNumber(), room.getPassword(), room.getMaxPeopleNumber());
    }

    public void deleteById(int roomNumber) {
        jdbcTemplate.update("DELETE FROM Room WHERE number=?",
                roomNumber);
    }
}
