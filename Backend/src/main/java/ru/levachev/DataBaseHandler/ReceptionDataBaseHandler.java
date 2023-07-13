package ru.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.levachev.Mapper.RoomMapper;
import ru.levachev.Model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static ru.levachev.Config.NSKZoneId;
import static ru.levachev.Config.hoursPerDay;
import static ru.levachev.DataBaseHandler.BookingBotDataBaseHandler.dayNumberRelativeToToday;

@Component
public class ReceptionDataBaseHandler extends DataBaseEntityAdder{
    private Booking lastBooking;

    private final JdbcTemplate jdbcTemplateGamesDB;
    private final JdbcTemplate jdbcTemplateOrganisationDB;

    @Autowired
    public ReceptionDataBaseHandler(JdbcTemplate jdbcTemplateOrganisationDB, JdbcTemplate jdbcTemplateGamesDB) {
        this.jdbcTemplateOrganisationDB=jdbcTemplateOrganisationDB;
        this.jdbcTemplateGamesDB = jdbcTemplateGamesDB;
    }

    public ReceptionCodeAnswer isBookingNumberValid(int bookingNumber) {
        lastBooking = jdbcTemplateOrganisationDB.query("SELECT * FROM Booking WHERE bookingNumber=?",
                        new Object[]{bookingNumber}, new BeanPropertyRowMapper<>(Booking.class))
                .stream().findAny().orElse(null);
        if(lastBooking == null){
            return ReceptionCodeAnswer.INVALID_ID;
        }
        if(isWrongTime()){
            return ReceptionCodeAnswer.WRONG_DATE;
        }
        return ReceptionCodeAnswer.SUCCESS;
    }

    private boolean isWrongTime(){
        if(lastBooking.getBeginTime() == 0){
            if(!lastBooking.getBeginDate().equals(LocalDate.now())){
                if(dayNumberRelativeToToday(lastBooking.getBeginDate()) == 1){
                    return isInTimeWindow();
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else{
            if(lastBooking.getBeginDate().equals(LocalDate.now())){
                return isInTimeWindow();
            } else {
                return false;
            }
        }
    }

    private boolean isInTimeWindow(){
        int currentHour = LocalDateTime.now(NSKZoneId).getHour();
        int currentMinute = LocalDateTime.now(NSKZoneId).getMinute();

        if(currentHour == lastBooking.getBeginTime()){
            return currentHour <= 20;
        } else if((currentHour+1)%hoursPerDay == lastBooking.getBeginTime()){
            return currentMinute >= 40;
        }
        return false;
    }

    public ClientInformation payBooking(int gameID, int peopleNumber) {
        takeGame(gameID);

        updateGameIDInBookingEntry(gameID);

        updateRoomDataByBookingNumber(lastBooking.getBookingNumber(), peopleNumber);

        if(!pay()){
            return null;
        }

        try{
            addPersonToTable(new Person(lastBooking.getPhoneNumber(), LocalDate.now()), jdbcTemplateOrganisationDB);
        } catch (DataAccessException ignored){
        }

        Room room = getRoom(lastBooking);
        if(room == null){
            return null;
        }

        return new ClientInformation(lastBooking.getRoomNumber(),
                lastBooking.getBeginTime(), lastBooking.getEndTime(),
                room.getPassword());
    }

    private Booking getBooking(){
       return jdbcTemplateOrganisationDB.query("SELECT * FROM Booking WHERE bookingNumber=?",
                        new Object[]{lastBooking.getBookingNumber()}, new BeanPropertyRowMapper<>(Booking.class))
                .stream().findAny().orElse(null);
    }

    private Room getRoom(Booking booking){
        return jdbcTemplateOrganisationDB.query("SELECT * FROM Room WHERE number=?",
                        new Object[]{booking.getRoomNumber()}, new RoomMapper()).
                stream().findAny().orElse(null);
    }

    private void updateGameIDInBookingEntry(int gameID){
        jdbcTemplateOrganisationDB.update("UPDATE Booking SET gameID=? WHERE bookingNumber=?",
                gameID, lastBooking.getBookingNumber());
    }

    private void updateRoomDataByBookingNumber(int bookingNumber, int peopleNumber){
        jdbcTemplateOrganisationDB.update("UPDATE bufferRoomData SET peopleNumber=?, \"isShouldChange\"=? WHERE roomNumber=?",
                peopleNumber, true, bookingNumber);
    }

    private void takeGame(int gameID){
        jdbcTemplateGamesDB.update("UPDATE Games SET isTaken=? WHERE id=?",
                true, gameID);
    }

    private boolean pay(){
        return true;
    }
}
