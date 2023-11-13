package scifidice.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.db.entities.Room;
import scifidice.db.mapper.RoomMapper;

import java.util.Optional;

@Component
public class RoomDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RoomDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public Optional<Room> getRoomByNumber(int number){
        return jdbcTemplate.query("SELECT * FROM Room WHERE number=?",
                        new Object[]{number}, new RoomMapper()).
                stream().findAny();
    }
    public void add(Room room, JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("INSERT INTO Room VALUES(?, ?, ?)",
                room.getNumber(), room.getPassword(), room.getMaxPeopleNumber());
    }
    public void updateCurrentPeopleNumberByRoomNumber(int roomNumber){
        jdbcTemplate.update("UPDATE Room SET currentpersonnumber=? WHERE number=?",
                0, roomNumber);
    }
    public void updatePassword(int roomNumber, String password){
        jdbcTemplate.update("UPDATE Room SET password=? WHERE number=?",
                password, roomNumber);
    }
    public void deleteById(int roomNumber) {
        jdbcTemplate.update("DELETE FROM Room WHERE number=?",
                roomNumber);
    }
}
