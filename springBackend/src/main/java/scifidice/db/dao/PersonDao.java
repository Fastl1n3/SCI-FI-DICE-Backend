package scifidice.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.db.entities.Person;
import scifidice.db.mapper.PersonMapper;

import java.util.List;
import java.util.Optional;

@Component
public class PersonDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Person> getPersonByPhoneNumber(String phoneNumber) {
        return jdbcTemplate.query("SELECT * FROM Person WHERE phoneNumber=?",
                new Object[]{phoneNumber}, new PersonMapper()).stream().findAny();
    }

    public Person getPersonByBookingBotChatId(String bookingChatId) {
        return jdbcTemplate.query("SELECT * FROM person WHERE bookingbot_chatid=?",
                new Object[]{bookingChatId}, new PersonMapper()).stream().findAny().orElse(null);
    }
}
