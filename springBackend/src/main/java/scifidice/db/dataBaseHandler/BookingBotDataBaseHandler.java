package scifidice.db.dataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.db.dao.BookingDao;
import scifidice.db.dao.PersonDao;
import scifidice.db.dao.RoomDao;
import scifidice.db.entities.Booking;
import scifidice.db.entities.Person;
import scifidice.db.entities.Room;
import scifidice.db.mapper.PersonMapper;
import scifidice.db.mapper.RoomMapper;
import scifidice.Entity.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.time.temporal.ChronoUnit.DAYS;
import static scifidice.config.SpringConfig.HOURS_PER_DAY;
import static scifidice.config.SpringConfig.NSK_ZONE_ID;
import static scifidice.db.dataBaseHandler.AutoUpdatableDataBaseHandler.addToTodayBeginBookingList;

@Component
public class BookingBotDataBaseHandler {

    private final PersonDao personDao;

    private final RoomDao roomDao;

    private final BookingDao bookingDao;


    @Autowired
    public BookingBotDataBaseHandler(PersonDao personDao, RoomDao roomDao, BookingDao bookingDao) {
        this.personDao = personDao;
        this.roomDao = roomDao;
        this.bookingDao = bookingDao;
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
        if (!isBookingValid(beginDate, endDate, beginTime, endTime, roomNumber)) {
            return -1;
        }

        String phoneNumber = getPhoneNumberByBookingChatID(bookingChatID);
        if (phoneNumber == null) {
            return -1;
        }

        int bookingNumber = generateUUID(roomNumber, beginTime, beginDate);

        Booking booking = new Booking(bookingNumber, phoneNumber, beginDate,
                endDate, beginTime, endTime, roomNumber, false);

        try {
            addBookingToTable(booking, jdbcTemplateOrganisationDB);
            if (booking.getBeginDate().isEqual(LocalDate.now(NSK_ZONE_ID))) {
                addToTodayBeginBookingList(booking);
            }
            updateRoomSchedule(booking.getRoomNumber(), booking.getBeginDate(), booking.getEndDate(), booking.getBeginTime(), booking.getEndTime());
        } catch (DataAccessException e) {
            return -1;
        }
        return bookingNumber;
    }

    private boolean isBookingValid(LocalDate beginDate, LocalDate endDate, int beginTime, int endTime, int roomNumber) {
        Room room = jdbcTemplateOrganisationDB.query("SELECT * FROM Room WHERE number=?",
                        new Object[]{roomNumber}, new RoomMapper()).
                stream().findAny().orElse(null);
        if (room == null) {
            return false;
        }
//TODO ;lkasdjfwqepoiurwqoitpeuhakjshdfkjahlsdgjhsakglskdjfg;sd;lkgxcmnv,biopqertopwieurtkjhdlfgsdfgkhjkljhsdfga[wpioeutqwpeoirut
        Boolean[] tmpArray = room.getSchedule();

        long beginDiff = dayNumberRelativeToToday(beginDate);

        if (beginDate.isEqual(endDate)) {
            for (int i = beginTime; i < endTime; i++) {
                if (tmpArray[(int) (beginDiff * HOURS_PER_DAY + i)]) {
                    return false;
                }
            }
        } else {
            long endDiff = dayNumberRelativeToToday(endDate);

            for (int i = beginTime; i < HOURS_PER_DAY; i++) {
                if (tmpArray[(int) (beginDiff * HOURS_PER_DAY + i)]) {
                    return false;
                }
            }
            for (int i = 0; i < endTime; i++) {
                if (tmpArray[(int) (endDiff * HOURS_PER_DAY + i)]) {
                    return false;
                }
            }
        }
        return true;
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

    public void updateRoomSchedule(int roomNumber, LocalDate beginDate, LocalDate endDate, int beginTime, int endTime) {
        Room room = jdbcTemplateOrganisationDB.query("SELECT * FROM Room WHERE number=?",
                        new Object[]{roomNumber}, new RoomMapper()).
                stream().findAny().orElse(null);
        Boolean[] tmpArray = room.getSchedule();

        if (beginDate.isEqual(endDate)) {
            for (int i = beginTime; i < endTime; i++) {
                tmpArray[(int) (dayNumberRelativeToToday(beginDate) * HOURS_PER_DAY + i)] = true;
            }
        } else {
            long beginDiff = abs(dayNumberRelativeToToday(beginDate));
            long endDiff = abs(dayNumberRelativeToToday(endDate));

            for (int i = beginTime; i < HOURS_PER_DAY; i++) {
                tmpArray[(int) (beginDiff * HOURS_PER_DAY + i)] = true;
            }
            for (int i = 0; i < endTime; i++) {
                tmpArray[(int) (endDiff * HOURS_PER_DAY + i)] = true;
            }
        }

        room.setSchedule(tmpArray);
        jdbcTemplateOrganisationDB.update("UPDATE Room SET schedule=? WHERE number=?", room.getSchedule(), roomNumber);
    }

    public ArrayList<HoursPair> getScheduleForDateByRoomNumber(LocalDate date, int roomNumber) throws WrongRoomNumberException {
//        if (dayNumber < 0 || dayNumber > 6) {
//            throw new DateTimeParseException("Wrong date", "", dayNumber);
//        }
//
//        Room room = jdbcTemplateOrganisationDB.query("SELECT * FROM Room WHERE number=?",
//                        new Object[]{roomNumber}, new RoomMapper()).
//                stream().findAny().orElse(null);
//        if (room == null) {
//            throw new WrongRoomNumberException("wrong room number");
//        }
//        return getHoursPairs(getRoomScheduleForDayByRoom(room, dayNumber).getSchedule(), dayNumber);
        return null;
    }

    private ArrayList<HoursPair> getHoursPairs(ArrayList<Boolean> schedule, int dayNumber) {
        ArrayList<HoursPair> hoursPairs = new ArrayList<>();

        boolean isOnStreak = false;

        int firstHour = 0;
        int secondHour = 0;
        int nowHours = LocalTime.now(NSK_ZONE_ID).getHour();

        for (int i = 0; i < schedule.size(); i++) {
            if (i < nowHours && dayNumber == 0) {
                continue;
            }
            if (!schedule.get(i)) {
                secondHour = i + 1;

                if (!isOnStreak) {
                    isOnStreak = true;
                    firstHour = i;
                }
            } else {
                if (isOnStreak) {
                    hoursPairs.add(new HoursPair(firstHour, secondHour));
                    isOnStreak = false;
                }
            }
        }

        if (isOnStreak) {
            hoursPairs.add(new HoursPair(firstHour, secondHour));
        }
        return hoursPairs;
    }

    public int updatePhoneNumberByBookingBotChatID(String phoneNumber, String bookingBotChatID) {
        return personDao.updatePhoneNumberByBookingBotChatID(phoneNumber, bookingBotChatID);
    }
}
