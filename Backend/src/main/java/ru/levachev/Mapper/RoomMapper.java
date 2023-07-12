package ru.levachev.Mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.levachev.Model.Room;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class RoomMapper implements RowMapper<Room> {
    public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
        Room room = new Room();

        room.setNumber(rs.getInt("number"));

        Array tmp = rs.getArray("schedule");
        room.setSchedule((Boolean[]) tmp.getArray());

        room.setPassword(rs.getString("password"));
        room.setCurrentPeopleNumber(rs.getInt("currentPersonNumber"));

        return room;
    }
}
