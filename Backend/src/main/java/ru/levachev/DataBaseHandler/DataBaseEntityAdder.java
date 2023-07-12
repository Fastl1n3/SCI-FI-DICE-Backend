package ru.levachev.DataBaseHandler;


import org.springframework.jdbc.core.JdbcTemplate;
import ru.levachev.Model.Booking;
import ru.levachev.Model.Person;
import ru.levachev.Model.Room;
import ru.levachev.Model.Game;

import java.sql.Date;

public class DataBaseEntityAdder {
    void addRoomToTable(Room room, JdbcTemplate jdbcTemplate){
        jdbcTemplate.update("INSERT INTO Room VALUES(?, ?, ?, ?)",
                room.getNumber(), room.getSchedule(), room.getPassword(),
                room.getCurrentPeopleNumber());
    }

    void addGameToTable(Game game, JdbcTemplate jdbcTemplate){
        jdbcTemplate.update("INSERT INTO Games VALUES(?, ?, ?, ?)",
                game.getId(), game.getName(),
                game.getRules(), game.isTaken());
    }

    void addBookingToTable(Booking booking, JdbcTemplate jdbcTemplate){
        jdbcTemplate.update("INSERT INTO Booking VALUES(?, ?, ?, ?, ?, ?, ?)",
                booking.getBookingNumber(), booking.getDate(),
                booking.getBeginTime(), booking.getEndTime(),
                booking.getPhoneNumber(), booking.getRoomNumber(),
                booking.getGameID());
    }

    void addPersonToTable(Person person, JdbcTemplate jdbcTemplate){
        jdbcTemplate.update("INSERT INTO Person VALUES(?, ?, ?, ?)",
                person.getPhoneNumber(), person.isBlackMark(),
                Date.valueOf(person.getLastVisit()), person.getDiscount());
    }
}
