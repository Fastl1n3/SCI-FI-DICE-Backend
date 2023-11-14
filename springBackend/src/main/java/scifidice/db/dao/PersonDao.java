package scifidice.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.db.entities.Person;
import scifidice.db.mapper.PersonMapper;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Component
public class PersonDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Person getPersonByPhoneNumber(String phoneNumber) {
        return jdbcTemplate.query("SELECT * FROM Person WHERE phone_number=?",
                new Object[]{phoneNumber}, new PersonMapper()).stream().findAny().orElse(null);
    }

    public Person getPersonByBookingBotChatId(String bookingChatId) {
        return jdbcTemplate.query("SELECT * FROM person WHERE bookingbot_chatid=?",
                new Object[]{bookingChatId}, new PersonMapper()).stream().findAny().orElse(null);
    }
    public void addPersonToTable(Person person) {
        jdbcTemplate.update("INSERT INTO Person VALUES(?, ?, ?, ?, ?, ?)",
                person.getPhoneNumber(), person.isBlackMark(),
                Date.valueOf(person.getLastVisit()), person.getDiscount(),
                person.getBookingBotChatID(), person.getInfoBotChatID());
    }
    public int updatePhoneNumberByBookingBotChatID(String phoneNumber, String bookingBotChatID) {
        return jdbcTemplate.update("UPDATE Person SET phone_number=? WHERE bookingbot_chatid=?",
                phoneNumber, bookingBotChatID);
    }
    public int setInfoBotChatIdByPhone(String phoneNumber, String infoBotChatID) {
        return jdbcTemplate.update("UPDATE Person SET infobot_chatid=? WHERE phone_number=?",
                infoBotChatID, phoneNumber);
    }

}
