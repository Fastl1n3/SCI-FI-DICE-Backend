package scifidice.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.db.entities.Booking;
import scifidice.db.mapper.BookingMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static scifidice.config.SpringConfig.NSK_ZONE_ID;

@Component
public class BookingDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookingDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Booking> getAll() {
        return jdbcTemplate.query("SELECT * FROM Booking",
                new Object[]{}, new BookingMapper());
    }

    public List<Booking> getAllByDate(Date date) {
        return jdbcTemplate.query("SELECT * FROM Booking WHERE begin_date=?",
                new Object[]{date}, new BookingMapper());
    }
}
