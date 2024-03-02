package scifidice.db.mapper;

import org.springframework.jdbc.core.RowMapper;
import scifidice.db.entities.Booking;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookingMapper implements RowMapper<Booking> {
    @Override
    public Booking mapRow(ResultSet resultSet, int i) throws SQLException {
        Booking booking = new Booking();

        booking.setBookingNumber(resultSet.getInt("booking_number"));
        booking.setBeginDate((resultSet.getDate("begin_date")).toLocalDate());
        booking.setEndDate((resultSet.getDate("end_date")).toLocalDate());
        booking.setBeginTime(resultSet.getInt("begin_time"));
        booking.setEndTime(resultSet.getInt("end_time"));
        booking.setPhoneNumber(resultSet.getString("phone_number"));
        booking.setRoomNumber(resultSet.getInt("room_number"));
        booking.setPaid(resultSet.getBoolean("isPaid"));

        return booking;
    }
}
