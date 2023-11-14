package scifidice.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.db.entities.Booking;
import scifidice.db.entities.Game;
import scifidice.db.mapper.GameMapper;

import java.util.List;

@Component
public class BookingAndGamesDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookingAndGamesDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Game> getGamesByBooking(Booking booking) {
        return jdbcTemplate.query("SELECT * FROM Booking_Games WHERE booking_number = ?",
                new Object[]{booking.getBookingNumber()}, new GameMapper());
    }
    public void deleteGamesByBookingId(int bookingId) {
        jdbcTemplate.update("DELETE FROM Booking_Games WHERE booking_number=?",
                bookingId);
    }
}
