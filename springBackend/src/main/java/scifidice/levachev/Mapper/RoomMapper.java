package scifidice.levachev.Mapper;

import org.springframework.jdbc.core.RowMapper;
import scifidice.levachev.Model.Room;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomMapper implements RowMapper<Room> {
    public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
        Room room = new Room();

        room.setNumber(rs.getInt("number"));

        Array tmp = rs.getArray("schedule");
        room.setSchedule((Boolean[]) tmp.getArray());

        room.setPassword(rs.getString("password"));
        room.setCurrentPersonNumber(rs.getInt("currentPersonNumber"));
        room.setMaxPersonNumber(rs.getInt("maxpersonnumber"));

        return room;
    }
}
