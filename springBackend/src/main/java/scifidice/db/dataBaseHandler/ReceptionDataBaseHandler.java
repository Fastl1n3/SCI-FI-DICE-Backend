package scifidice.db.dataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scifidice.Entity.ClientInformation;
import scifidice.Entity.ReceptionCodeAnswer;
import scifidice.db.dao.BookingDao;
import scifidice.db.dao.GameDao;
import scifidice.db.dao.RoomDao;
import scifidice.db.entities.Booking;
import scifidice.db.entities.Game;
import scifidice.db.entities.Room;
import scifidice.db.mapper.RoomMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static scifidice.config.SpringConfig.NSK_ZONE_ID;
import static scifidice.db.dataBaseHandler.AutoUpdatableDataBaseHandler.getTodayBeginBookingList;

@Component
public class ReceptionDataBaseHandler extends DataBaseEntityAdder {
    private final BookingDao bookingDao;
    private final GameDao gameDao;
    private final RoomDao roomDao;
    private final InfoSender infoSender;
    private Booking lastBooking;

    @Autowired
    public ReceptionDataBaseHandler(BookingDao bookingDao, GameDao gameDao, RoomDao roomDao, InfoSender infoSender) {
        this.bookingDao = bookingDao;
        this.gameDao = gameDao;
        this.roomDao = roomDao;
        this.infoSender = infoSender;
    }


    public ReceptionCodeAnswer isBookingNumberValid(int bookingNumber) {
        lastBooking = bookingDao.getByBookingNumber(bookingNumber);
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
        Game game = gameDao.getGameByGameId(id);
        return game != null && !game.isTaken();
    }

    public ClientInformation payBooking(int gameID, int peopleNumber) {
        Room room = getRoom(lastBooking);

        if (peopleNumber > room.getMaxPersonNumber()) {
            return new ClientInformation(
                    0, 0, 0, null, ReceptionCodeAnswer.ILLEGAL_PEOPLE_NUMBER
            );
        }

        if (!isGameIDValid(gameID)) {
            return new ClientInformation(
                    0, 0, 0, null, ReceptionCodeAnswer.INVALID_GAME_ID
            );
        }

        if (!pay()) {
            return new ClientInformation(
                    0, 0, 0, null, ReceptionCodeAnswer.FAILED_PAY
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

        return new ClientInformation(lastBooking.getRoomNumber(),
                lastBooking.getBeginTime(), lastBooking.getEndTime(),
                room.getPassword(), ReceptionCodeAnswer.SUCCESS);
    }

    public ReceptionCodeAnswer addPeople(int peopleNumber) {
        int oldPeopleNumber;
        int newPeopleNumber;

        Room room = getRoom(lastBooking);

        if (isLate()) {
            oldPeopleNumber = room.getCurrentPersonNumber();
        } else {
            Room roomData = getRoom(lastBooking);
            oldPeopleNumber = roomData.getCurrentPersonNumber();
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
        return roomDao.getRoomByNumber(booking.getRoomNumber());
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
