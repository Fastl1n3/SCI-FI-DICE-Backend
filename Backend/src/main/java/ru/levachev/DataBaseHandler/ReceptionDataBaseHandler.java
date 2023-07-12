package ru.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.levachev.Mapper.RoomMapper;
import ru.levachev.Model.Booking;
import ru.levachev.Model.ClientInformation;
import ru.levachev.Model.Person;
import ru.levachev.Model.Room;

import java.time.LocalDate;
import java.util.*;

@Component
public class ReceptionDataBaseHandler extends DataBaseEntityAdder{
    private Map<Integer, Integer> roomData;
    private final JdbcTemplate jdbcTemplateOrganisationDB;
    private int lastPersonBookingNumber;

    @Autowired
    public ReceptionDataBaseHandler(JdbcTemplate jdbcTemplateOrganisationDB) {
        this.jdbcTemplateOrganisationDB = jdbcTemplateOrganisationDB;
        roomData = new HashMap<>();
    }

    public boolean isBookingNumberValid(int bookingNumber) {
        lastPersonBookingNumber = bookingNumber;
        Booking booking = jdbcTemplateOrganisationDB.query("SELECT * FROM Booking WHERE bookingNumber=?",
                        new Object[]{bookingNumber}, new BeanPropertyRowMapper<>(Booking.class))
                .stream().findAny().orElse(null);
        return booking != null;
    }

    public ClientInformation payBooking(int gameID, int peopleNumber) {
        Booking booking = getBooking();

        if (booking == null) {
            System.out.println("1");
            return null;
        }

        updateGameIDInBookingEntry(gameID);

        updateRoomDataByBookingNumber(booking.getBookingNumber(), peopleNumber);

        if(!pay()){
            System.out.println("2");
            return null;
        }

        try{
            addPersonToTable(new Person(booking.getPhoneNumber(), LocalDate.now()), jdbcTemplateOrganisationDB);
        } catch (DataAccessException ignored){
        }

        Room room = getRoom(booking);
        if(room == null){
            System.out.println("3");
            return null;
        }

        return new ClientInformation(booking.getRoomNumber(),
                booking.getBeginTime(), booking.getEndTime(),
                room.getPassword());
    }

    private Booking getBooking(){
       return jdbcTemplateOrganisationDB.query("SELECT * FROM Booking WHERE bookingNumber=?",
                        new Object[]{lastPersonBookingNumber}, new BeanPropertyRowMapper<>(Booking.class))
                .stream().findAny().orElse(null);
    }

    private Room getRoom(Booking booking){
        return jdbcTemplateOrganisationDB.query("SELECT * FROM Room WHERE number=?",
                        new Object[]{booking.getBookingNumber()}, new RoomMapper()).
                stream().findAny().orElse(null);
    }

    private void updateGameIDInBookingEntry(int gameID){
        jdbcTemplateOrganisationDB.update("UPDATE Booking SET gameID=? WHERE bookingNumber=?",
                gameID, lastPersonBookingNumber);
    }

    private void updateRoomDataByBookingNumber(int bookingNumber, int peopleNumber){
        jdbcTemplateOrganisationDB.update("UPDATE bufferedRoomData SET peopleNumber=?, isShouldChange=? WHERE roomNumber=?",
                peopleNumber, true, bookingNumber);
    }

    private boolean pay(){
        return true;
    }
}
