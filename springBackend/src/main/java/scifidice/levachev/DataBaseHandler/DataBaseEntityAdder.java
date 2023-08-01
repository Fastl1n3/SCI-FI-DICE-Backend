package scifidice.levachev.DataBaseHandler;

import org.springframework.jdbc.core.JdbcTemplate;
import scifidice.levachev.Model.*;

import java.sql.Date;

public class DataBaseEntityAdder {
    void addRoomToTable(Room room, JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("INSERT INTO Room VALUES(?, ?, ?, ?, ?)",
                room.getNumber(), room.getSchedule(), room.getPassword(),
                room.getCurrentPersonNumber(), room.getMaxPersonNumber());
    }

    void addGameToTable(Game game, JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("INSERT INTO Games VALUES(?, ?, ?, ?)",
                game.getId(), game.getName(),
                game.getRules(), game.isTaken());
    }

    void addBookingToTable(Booking booking, JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("INSERT INTO Booking VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                booking.getBookingNumber(), booking.getBeginDate(),
                booking.getEndDate(), booking.getBeginTime(),
                booking.getEndTime(), booking.getPhoneNumber(),
                booking.getRoomNumber(), booking.getGameID(),
                booking.isPaid());
    }

    void addPersonToTable(Person person, JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("INSERT INTO Person VALUES(?, ?, ?, ?, ?, ?)",
                person.getPhoneNumber(), person.isBlackMark(),
                Date.valueOf(person.getLastVisit()), person.getDiscount(),
                person.getBookingBotChatID(), person.getInfoBotChatID());
    }

    void addRoomDataToTable(BufferRoomData roomData, JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("INSERT INTO bufferRoomData VALUES(?, ?, ?)",
                roomData.getRoomNumber(), roomData.getPeopleNumber(),
                roomData.isShouldChange());
    }
}
