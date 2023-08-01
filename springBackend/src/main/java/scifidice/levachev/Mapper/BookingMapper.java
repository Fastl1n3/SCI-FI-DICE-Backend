package scifidice.levachev.Mapper;

import org.springframework.jdbc.core.RowMapper;
import scifidice.levachev.Model.Booking;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookingMapper implements RowMapper<Booking> {
    @Override
    public Booking mapRow(ResultSet resultSet, int i) throws SQLException {
        Booking booking = new Booking();

        booking.setBookingNumber(resultSet.getInt("bookingNumber"));
        booking.setBeginDate((resultSet.getDate("beginDate")).toLocalDate());
        booking.setEndDate((resultSet.getDate("endDate")).toLocalDate());
        booking.setBeginTime(resultSet.getInt("beginTime"));
        booking.setEndTime(resultSet.getInt("endTime"));
        booking.setPhoneNumber(resultSet.getString("phoneNumber"));
        booking.setRoomNumber(resultSet.getInt("roomNumber"));
        booking.setGameID(resultSet.getInt("gameID"));
        booking.setPaid(resultSet.getBoolean("ispaid"));

        return booking;
    }
}
