package scifidice.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.db.entities.Room;
import scifidice.db.mapper.GameMapper;
import scifidice.db.mapper.RoomMapper;

import java.util.List;

@Component
public class RoomDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RoomDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public boolean checkEmpty() {
        return jdbcTemplate.query("SELECT * FROM Room LIMIT 1", new Object[]{}, new RoomMapper()).isEmpty();
    }
    public Room getRoomByNumber(int number) {
        return jdbcTemplate.query("SELECT * FROM Room WHERE number=?",
                        new Object[]{number}, new RoomMapper()).
                stream().findAny().orElse(null);
    }

    public List<Room> getAllRooms() {
        return jdbcTemplate.query("SELECT * FROM Room", new Object[]{}, new RoomMapper());
    }

    public void add(Room room) {
        jdbcTemplate.update("INSERT INTO Room VALUES(?, ?, ?)",
                room.getNumber(), room.getPassword(), room.getMaxPeopleNumber());
    }

    public void updatePassword(int roomNumber, String password) {
        jdbcTemplate.update("UPDATE Room SET password=? WHERE number=?",
                password, roomNumber);
    }

    public void deleteRoomByNumber(int roomNumber) {
        jdbcTemplate.update("DELETE FROM Room WHERE number=?",
                roomNumber);
    }
}
