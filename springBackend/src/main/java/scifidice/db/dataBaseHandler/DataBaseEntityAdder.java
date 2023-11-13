package scifidice.db.dataBaseHandler;

import org.springframework.jdbc.core.JdbcTemplate;
import scifidice.db.entities.Booking;
import scifidice.db.entities.Game;
import scifidice.db.entities.Person;
import scifidice.db.entities.Room;

import java.sql.Date;

public class DataBaseEntityAdder {
    void addRoomToTable(Room room, JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("INSERT INTO Room VALUES(?, ?, ?)",
                room.getNumber(), room.getPassword(), room.getMaxPeopleNumber());
    }

    void addGameToTable(Game game, JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("INSERT INTO Games VALUES(?, ?, ?, ?)",
                game.getGameId(), game.getName(),
                game.getRules(), game.isTaken());
    }

    void addBookingToTable(Booking booking, JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("INSERT INTO Booking VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                booking.getBookingNumber(), booking.getBeginDate(),
                booking.getEndDate(), booking.getBeginTime(),
                booking.getEndTime(), booking.getPhoneNumber(),
                booking.getRoomNumber(), booking.getCurrentPeopleNumber(),
                booking.isPaid());
    }

    void addPersonToTable(Person person, JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("INSERT INTO Person VALUES(?, ?, ?, ?, ?, ?)",
                person.getPhoneNumber(), person.isBlackMark(),
                Date.valueOf(person.getLastVisit()), person.getDiscount(),
                person.getBookingBotChatID(), person.getInfoBotChatID());
    }
}
