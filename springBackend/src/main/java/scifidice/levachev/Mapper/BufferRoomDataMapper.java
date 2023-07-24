package scifidice.levachev.Mapper;

import org.springframework.jdbc.core.RowMapper;
import scifidice.levachev.Model.BufferRoomData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BufferRoomDataMapper implements RowMapper<BufferRoomData> {
    @Override
    public BufferRoomData mapRow(ResultSet rs, int rowNum) throws SQLException {
        BufferRoomData bufferRoomData = new BufferRoomData();

        bufferRoomData.setRoomNumber(rs.getInt("roomNumber"));
        bufferRoomData.setPeopleNumber(rs.getInt("peopleNumber"));
        bufferRoomData.setShouldChange(rs.getBoolean("isShouldChange"));

        return bufferRoomData;
    }
}
