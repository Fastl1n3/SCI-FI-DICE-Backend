package scifidice.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.levachev.Mapper.BufferRoomDataMapper;
import scifidice.levachev.Mapper.RoomMapper;
import scifidice.levachev.Mapper.BookingMapper;
import scifidice.levachev.Mapper.GameMapper;
import scifidice.levachev.Model.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static scifidice.burym.config.SpringConfig.NSK_ZONE_ID;
import static scifidice.levachev.DataBaseHandler.AutoUpdatableDataBaseHandler.getTodayBeginBookingList;

@Component
public class ReceptionDataBaseHandler extends DataBaseEntityAdder {
    private Booking lastBooking;
    private final JdbcTemplate jdbcTemplateGamesDB;
    private final JdbcTemplate jdbcTemplateOrganisationDB;

    @Autowired
    private InfoSender infoSender;

    @Autowired
    public ReceptionDataBaseHandler(JdbcTemplate jdbcTemplateOrganisationDB, JdbcTemplate jdbcTemplateGamesDB) {
        this.jdbcTemplateOrganisationDB = jdbcTemplateOrganisationDB;
        this.jdbcTemplateGamesDB = jdbcTemplateGamesDB;
    }

    public ReceptionCodeAnswer isBookingNumberValid(int bookingNumber) {
        lastBooking = jdbcTemplateOrganisationDB.query("SELECT * FROM Booking WHERE bookingNumber=?",
                        new Object[]{bookingNumber}, new BookingMapper())
                .stream().findAny().orElse(null);
        if (lastBooking == null) {
            return ReceptionCodeAnswer.INVALID_ID;
        }
        if (!isWrongTime()) {
            return ReceptionCodeAnswer.WRONG_DATE;
        }
        if (lastBooking.isPaid()) {
            return ReceptionCodeAnswer.PAID;
        }
        return ReceptionCodeAnswer.SUCCESS;
    }

    private boolean isWrongTime() {
        LocalDateTime beginDateTime = LocalDateTime.of(lastBooking.getBeginDate(),
                LocalTime.of(lastBooking.getBeginTime(), 0, 0, 0));

        LocalDate endDate = lastBooking.getEndDate();
        int endTime = lastBooking.getEndTime();
        if (lastBooking.getEndTime() == 24) {
            endTime = 0;
            endDate = endDate.plusDays(1);
        }

        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.of(endTime, 0, 0));

        LocalDateTime nowDateTime = LocalDateTime.now(NSK_ZONE_ID);

        LocalDateTime under20BeginDateTime = beginDateTime.minusMinutes(20);

        return !nowDateTime.isBefore(under20BeginDateTime) && !nowDateTime.isAfter(endDateTime);
    }


    private boolean isGameIDValid(int id) {
        Game game = jdbcTemplateGamesDB.query("SELECT * FROM Games WHERE id=?",
                        new Object[]{id}, new GameMapper())
                .stream().findAny().orElse(null);
        return game != null && !game.isTaken();
    }

    public ClientInformation payBooking(int gameID, int peopleNumber) {
        Room room = getRoom(lastBooking);

        if (peopleNumber > room.getMaxPersonNumber()) {
            return new ClientInformation(
                    ReceptionCodeAnswer.ILLEGAL_PEOPLE_NUMBER, 0, 0, 0, null
            );
        }

        if (!isGameIDValid(gameID)) {
            return new ClientInformation(
                    ReceptionCodeAnswer.INVALID_GAME_ID, 0, 0, 0, null
            );
        }

        if (!pay()) {
            return new ClientInformation(
                    ReceptionCodeAnswer.FAILED_PAY, 0, 0, 0, null
            );
        }

        takeGame(gameID);

        updateBookingEntry(gameID, true);

        updateLastVisit(lastBooking.getPhoneNumber());

        if (isLate()) {
            updateCurrentPeopleNumberByRoomNumber(lastBooking.getRoomNumber(), peopleNumber);
            try {
                infoSender.sendToAdminRoomInfo(lastBooking.getRoomNumber(), getTodayBeginBookingList());
            } catch (WrongRoomNumberException e) {
                System.out.println(e.getMessage());
            }
        } else {
            updateRoomDataByRoomNumber(lastBooking.getRoomNumber(), peopleNumber);
        }
        /////////////////////////////////////////////

        return new ClientInformation(ReceptionCodeAnswer.SUCCESS, lastBooking.getRoomNumber(),
                lastBooking.getBeginTime(), lastBooking.getEndTime(),
                room.getPassword());
    }

    public ReceptionCodeAnswer addPeople(int peopleNumber) {
        int oldPeopleNumber;
        int newPeopleNumber;

        Room room = getRoom(lastBooking);

        if (isLate()) {
            oldPeopleNumber = room.getCurrentPersonNumber();
        } else {
            BufferRoomData bufferRoomData = getBufferRoomData(lastBooking);
            oldPeopleNumber = bufferRoomData.getPeopleNumber();
        }

        newPeopleNumber = oldPeopleNumber + peopleNumber;

        if (newPeopleNumber > room.getMaxPersonNumber()) {
            return ReceptionCodeAnswer.ILLEGAL_PEOPLE_NUMBER;
        }

        if (!pay()) {
            return ReceptionCodeAnswer.FAILED_PAY;
        }

        if (isLate()) {
            updateCurrentPeopleNumberByRoomNumber(lastBooking.getRoomNumber(), newPeopleNumber);
            try {
                infoSender.sendToAdminRoomInfo(lastBooking.getRoomNumber(), getTodayBeginBookingList());
            } catch (WrongRoomNumberException e) {
                System.out.println(e.getMessage());
            }
        } else {
            updateRoomDataByRoomNumber(lastBooking.getRoomNumber(), newPeopleNumber);
        }

        return ReceptionCodeAnswer.SUCCESS;
    }

    private boolean isLate() {
        LocalDateTime beginTime = LocalDateTime.of(lastBooking.getBeginDate(),
                LocalTime.of(lastBooking.getBeginTime(), 0, 0, 0));

        LocalDateTime nowTime = LocalDateTime.now(NSK_ZONE_ID);

        return !nowTime.isBefore(beginTime);
    }


    private Room getRoom(Booking booking) {
        return jdbcTemplateOrganisationDB.query("SELECT * FROM Room WHERE number=?",
                        new Object[]{booking.getRoomNumber()}, new RoomMapper()).
                stream().findAny().orElse(null);
    }

    private BufferRoomData getBufferRoomData(Booking booking) {
        return jdbcTemplateOrganisationDB.query("SELECT * FROM bufferroomdata WHERE roomnumber=?",
                        new Object[]{booking.getRoomNumber()}, new BufferRoomDataMapper()).
                stream().findAny().orElse(null);
    }


    private void updateBookingEntry(int gameID, boolean isPaid) {
        jdbcTemplateOrganisationDB.update("UPDATE Booking SET gameID=?, ispaid=? WHERE bookingNumber=?",
                gameID, isPaid, lastBooking.getBookingNumber());

        List<Booking> bookingList = getTodayBeginBookingList();

        for (Booking booking : bookingList) {
            if (booking.getBookingNumber() == lastBooking.getBookingNumber()) {
                booking.setPaid(true);
                booking.setGameID(gameID);
                return;
            }
        }
    }

    private void updateRoomDataByRoomNumber(int roomNumber, int peopleNumber) {
        jdbcTemplateOrganisationDB.update("UPDATE bufferRoomData SET peopleNumber=?, isShouldChange=? WHERE roomNumber=?",
                peopleNumber, true, roomNumber);
    }

    private void updateCurrentPeopleNumberByRoomNumber(int roomNumber, int currentPeopleNumber) {
        jdbcTemplateOrganisationDB.update("UPDATE Room SET currentPersonNumber=? WHERE number=?",
                currentPeopleNumber, roomNumber);
    }

    private void takeGame(int gameID) {
        jdbcTemplateGamesDB.update("UPDATE Games SET isTaken=? WHERE id=?",
                true, gameID);
    }

    private void updateLastVisit(String phoneNumber) {
        jdbcTemplateOrganisationDB.update("UPDATE Person SET lastvisit=? WHERE phonenumber=?",
                Date.valueOf(LocalDate.now(NSK_ZONE_ID)), phoneNumber);
    }

    private boolean pay() {
        return true;
    }
}
