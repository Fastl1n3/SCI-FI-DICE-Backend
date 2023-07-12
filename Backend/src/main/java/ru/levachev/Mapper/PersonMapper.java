package ru.levachev.Mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.levachev.Model.Person;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

public class PersonMapper implements RowMapper<Person> {
    @Override
    public Person mapRow(ResultSet resultSet, int i) throws SQLException {
        Person person = new Person();

        person.setPhoneNumber(resultSet.getString("phoneNumber"));
        person.setBlackMark(resultSet.getBoolean("blackMark"));
        person.setLastVisit(resultSet.getDate("lastVisit").toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        person.setDiscount(resultSet.getInt("discount"));

        return person;
    }
}
