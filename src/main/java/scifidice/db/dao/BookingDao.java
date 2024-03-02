package scifidice.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.db.entities.Booking;
import scifidice.db.mapper.BookingMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class BookingDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookingDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Booking getByBookingNumber(int bookingNumber) {
        return jdbcTemplate.query("SELECT * FROM Booking WHERE booking_number=?",
                        new Object[]{bookingNumber}, new BookingMapper())
                .stream().findAny().orElse(null);
    }

    public Booking getBookingByRoomAndDate(int room, LocalDateTime dateTime) {
        LocalDate date = LocalDate.from(dateTime);
        int hour = dateTime.getHour();

        return jdbcTemplate.query("SELECT * FROM Booking WHERE room_number=? AND begin_date=? AND begin_time >= ? AND end_time < ?",
                        new Object[]{room, Date.valueOf(date), hour, hour}, new BookingMapper())
                .stream().findAny().orElse(null);
    }

    public Booking getByRoomNumber(int roomNumber) {
        return jdbcTemplate.query("SELECT * FROM Booking WHERE room_number=?",
                new Object[]{roomNumber}, new BookingMapper()).stream().findAny().orElse(null);
    }

    public List<Booking> getAll() {
        return jdbcTemplate.query("SELECT * FROM Booking",
                new Object[]{}, new BookingMapper());
    }

    public List<Booking> getAllByDate(Date date) {
        return jdbcTemplate.query("SELECT * FROM Booking WHERE begin_date=?",
                new Object[]{date}, new BookingMapper());
    }

    public void add(Booking booking) {
        jdbcTemplate.update("INSERT INTO Booking VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                booking.getBookingNumber(), booking.getBeginDate(),
                booking.getEndDate(), booking.getBeginTime(),
                booking.getEndTime(), booking.getPhoneNumber(),
                booking.getRoomNumber(), booking.getCurrentPeopleNumber(),
                booking.isPaid());
    }

    public void updateCurrentPeopleByBookingNumber(int currentPeople, int bookingNumber) {
        jdbcTemplate.update("UPDATE Booking SET current_people=? WHERE booking_number=?",
                currentPeople, bookingNumber);
    }

    public void updatePaidByBookingNumber(int bookingNumber, boolean isPaid) {
        jdbcTemplate.update("UPDATE Booking SET gameID=?, ispaid=? WHERE bookingNumber=?",
                 isPaid, bookingNumber);
    }
    public void deleteByBooking(Booking booking) {
        jdbcTemplate.update("DELETE FROM Booking WHERE booking_number=?",
                booking.getBookingNumber());
    }


}
