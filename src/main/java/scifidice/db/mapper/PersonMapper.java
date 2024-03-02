package scifidice.db.mapper;

import org.springframework.jdbc.core.RowMapper;
import scifidice.db.entities.Person;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonMapper implements RowMapper<Person> {
    @Override
    public Person mapRow(ResultSet resultSet, int i) throws SQLException {
        Person person = new Person();

        person.setPhoneNumber(resultSet.getString("phone_number"));
        person.setBlackMark(resultSet.getBoolean("black_mark"));
        person.setLastVisit(resultSet.getDate("last_visit").toLocalDate());
        person.setDiscount(resultSet.getInt("discount"));
        person.setBookingBotChatID(resultSet.getString("bookingbot_chatid"));
        person.setInfoBotChatID(resultSet.getString("infobot_chatid"));

        return person;
    }
}
