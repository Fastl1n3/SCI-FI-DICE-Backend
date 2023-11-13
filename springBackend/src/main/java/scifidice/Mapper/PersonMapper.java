package scifidice.Mapper;

import org.springframework.jdbc.core.RowMapper;
import scifidice.Entity.Person;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonMapper implements RowMapper<Person> {
    @Override
    public Person mapRow(ResultSet resultSet, int i) throws SQLException {
        Person person = new Person();

        person.setPhoneNumber(resultSet.getString("phoneNumber"));
        person.setBlackMark(resultSet.getBoolean("blackMark"));
        person.setLastVisit(resultSet.getDate("lastVisit").toLocalDate());
        person.setDiscount(resultSet.getInt("discount"));
        person.setBookingBotChatID(resultSet.getString("bookingBotChatID"));
        person.setInfoBotChatID(resultSet.getString("infoBotChatID"));

        return person;
    }
}
