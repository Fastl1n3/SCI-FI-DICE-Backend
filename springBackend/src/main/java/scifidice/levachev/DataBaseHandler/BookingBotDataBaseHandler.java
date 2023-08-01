package scifidice.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.levachev.Mapper.PersonMapper;
import scifidice.levachev.Mapper.RoomMapper;
import scifidice.levachev.Model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.time.temporal.ChronoUnit.DAYS;
import static scifidice.burym.config.SpringConfig.HOURS_PER_DAY;
import static scifidice.burym.config.SpringConfig.NSK_ZONE_ID;
import static scifidice.levachev.DataBaseHandler.AutoUpdatableDataBaseHandler.addToTodayBeginBookingList;

@Component
public class BookingBotDataBaseHandler extends DataBaseEntityAdder {
    private final JdbcTemplate jdbcTemplateOrganisationDB;

    @Autowired
    public BookingBotDataBaseHandler(JdbcTemplate jdbcTemplateOrganisationDB) {
        this.jdbcTemplateOrganisationDB = jdbcTemplateOrganisationDB;
    }

    public static long dayNumberRelativeToToday(LocalDate date) {
        return DAYS.between(LocalDate.now(NSK_ZONE_ID), date);
    }

    public void authorization(String phoneNumber, String bookingBotChatID) throws DataAccessException {
        addPersonToTable(new Person(phoneNumber, bookingBotChatID), jdbcTemplateOrganisationDB);
    }

    public boolean isBlackMarkPerson(String bookingChatID) {
        Person person = jdbcTemplateOrganisationDB.query("SELECT * FROM person WHERE bookingbotchatid=?",
                new Object[]{bookingChatID}, new PersonMapper()).stream().findAny().orElse(null);
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
        Person person = jdbcTemplateOrganisationDB.query("SELECT * FROM Person WHERE bookingBotChatID=?",
                        new Object[]{bookingBotChatID}, new PersonMapper()).
                stream().findAny().orElse(null);
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

    public ArrayList<HoursPair> getScheduleForDateByRoomNumber(int dayNumber, int roomNumber) throws WrongRoomNumberException {
        if (dayNumber < 0 || dayNumber > 6) {
            throw new DateTimeParseException("Wrong date", "", dayNumber);
        }

        Room room = jdbcTemplateOrganisationDB.query("SELECT * FROM Room WHERE number=?",
                        new Object[]{roomNumber}, new RoomMapper()).
                stream().findAny().orElse(null);
        if (room == null) {
            throw new WrongRoomNumberException("wrong room number");
        }
        return getHoursPairs(getRoomScheduleForDayByRoom(room, dayNumber).getSchedule(), dayNumber);
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

    private RoomScheduleForDay getRoomScheduleForDayByRoom(Room room, int dayNumber) {
        ArrayList<Boolean> arrayList = new ArrayList<>();
        for (int i = 0; i < HOURS_PER_DAY; i++) {
            arrayList.add(room.getSchedule()[(dayNumber) * HOURS_PER_DAY + i]);
        }
        return new RoomScheduleForDay(arrayList);
    }

    public int updatePhoneNumberByBookingBotChatID(String phoneNumber, String bookingBotChatID) {
        return jdbcTemplateOrganisationDB.update("UPDATE Person SET phonenumber=? WHERE bookingbotchatid=?",
                phoneNumber, bookingBotChatID);
    }
}
