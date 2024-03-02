package scifidice.db.mapper;

import org.springframework.jdbc.core.RowMapper;
import scifidice.db.entities.Room;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomMapper implements RowMapper<Room> {
    public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
        Room room = new Room();

        room.setNumber(rs.getInt("number"));

        room.setPassword(rs.getString("password"));
        room.setMaxPeopleNumber(rs.getInt("max_people"));

        return room;
    }
}
