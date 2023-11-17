package scifidice.db.dataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import scifidice.db.dao.BookingDao;
import scifidice.db.dao.PersonDao;
import scifidice.db.entities.Booking;
import scifidice.db.entities.Person;;
import scifidice.Entity.*;
import scifidice.model.BookingTime;

import java.time.LocalDate;
import java.util.ArrayList;


import static java.time.temporal.ChronoUnit.DAYS;
import static scifidice.config.SpringConfig.NSK_ZONE_ID;


@Component
public class BookingBotDataBaseHandler {

    private final PersonDao personDao;

    private final BookingDao bookingDao;

    private final BookingTime bookingTime;

    @Autowired
    public BookingBotDataBaseHandler(PersonDao personDao, BookingDao bookingDao, BookingTime bookingTime) {
        this.personDao = personDao;
        this.bookingDao = bookingDao;
        this.bookingTime = bookingTime;
    }

    public static long dayNumberRelativeToToday(LocalDate date) {
        return DAYS.between(LocalDate.now(NSK_ZONE_ID), date);
    }

    public void authorization(String phoneNumber, String bookingBotChatID) throws DataAccessException {
        personDao.addPersonToTable(new Person(phoneNumber, bookingBotChatID));
    }

    public boolean isBlackMarkPerson(String bookingChatID) {
        Person person = personDao.getPersonByBookingBotChatId(bookingChatID);
        return person.isBlackMark();
    }

    public int book(String bookingChatID, LocalDate beginDate, LocalDate endDate, int beginTime, int endTime, int roomNumber) {
        String phoneNumber = getPhoneNumberByBookingChatID(bookingChatID);
        if (phoneNumber == null) {
            return -1;
        }
        if (bookingTime.addNewReserving(beginDate, new HoursPair(beginTime, endTime), roomNumber) == -1) {
            return -1;
        }
        int bookingNumber = generateUUID(roomNumber, beginTime, beginDate);

        Booking booking = new Booking(bookingNumber, phoneNumber, beginDate,
                endDate, beginTime, endTime, roomNumber, false);

        try {
            bookingDao.add(booking);
        } catch (DataAccessException e) {
            return -1;
        }
        return bookingNumber;
    }

    public String getPhoneNumberByBookingChatID(String bookingBotChatID) {
        Person person = personDao.getPersonByBookingBotChatId(bookingBotChatID);
        if (person == null) {
            return null;
        } else {
            return person.getPhoneNumber();
        }
    }

    private int generateUUID(int roomNumber, int beginTime, LocalDate beginDate) {
        return (beginDate.getYear() * 10000 + beginDate.getMonthValue() * 1000 + beginDate.getDayOfMonth() * 100 + roomNumber * 10 + beginTime);
    }

    public ArrayList<HoursPair> getScheduleForDateByRoomNumber(LocalDate date, int roomNumber) throws WrongRoomNumberException {
        return bookingTime.getFreeTimeByDate(date, roomNumber);
    }

    public int updatePhoneNumberByBookingBotChatID(String phoneNumber, String bookingBotChatID) {
        return personDao.updatePhoneNumberByBookingBotChatID(phoneNumber, bookingBotChatID);
    }
}
