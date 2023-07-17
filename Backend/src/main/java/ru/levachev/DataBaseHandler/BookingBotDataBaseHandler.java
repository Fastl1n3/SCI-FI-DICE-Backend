package ru.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.levachev.Mapper.PersonMapper;
import ru.levachev.Model.Booking;
import ru.levachev.Model.Person;
import ru.levachev.Model.Room;
import ru.levachev.Model.RoomScheduleForDay;
import ru.levachev.Mapper.RoomMapper;

import static java.lang.Math.abs;
import static java.time.temporal.ChronoUnit.DAYS;
import static ru.levachev.Config.hoursPerDay;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookingBotDataBaseHandler extends DataBaseEntityAdder {
    private final JdbcTemplate jdbcTemplateOrganisationDB;

    @Autowired
    public BookingBotDataBaseHandler(JdbcTemplate jdbcTemplateOrganisationDB){
        this.jdbcTemplateOrganisationDB=jdbcTemplateOrganisationDB;
    }

    public static long dayNumberRelativeToToday(LocalDate date){
        return DAYS.between(date, LocalDate.now());
    }

    public boolean authorization(String phoneNumber, String bookingBotChatID){
        try{
            addPersonToTable(new Person(phoneNumber, bookingBotChatID), jdbcTemplateOrganisationDB);
            return true;
        } catch (DataAccessException ignored){
            return false;
        }
    }

    public int book(String bookingChatID, LocalDate beginDate, LocalDate endDate, int beginTime, int endTime, int roomNumber){
        String phoneNumber = getPhoneNumberByBookingChatID(bookingChatID);
        if(phoneNumber == null){
            return -1;
        }

        int bookingNumber = generateUUID(roomNumber, beginTime, beginDate);

        Booking booking = new Booking(bookingNumber, phoneNumber, beginDate,
                endDate, beginTime, endTime, roomNumber);

        try{
            addBookingToTable(booking, jdbcTemplateOrganisationDB);
            updateRoomSchedule(booking.getRoomNumber(), booking.getBeginDate(), booking.getEndDate(), booking.getBeginTime(), booking.getEndTime());
        } catch (DataAccessException ignored){
            return -1;
        }
        return bookingNumber;
    }

    private String getPhoneNumberByBookingChatID(String bookingBotChatID){
        Person person = jdbcTemplateOrganisationDB.query("SELECT * FROM Person WHERE bookingBotChatID=?",
                        new Object[]{bookingBotChatID}, new PersonMapper()).
                stream().findAny().orElse(null);
        if(person == null){
            return null;
        } else {
            return person.getPhoneNumber();
        }
    }

    private int generateUUID(int roomNumber, int beginTime, LocalDate beginDate){
        return (beginDate.getYear()*10000+beginDate.getMonthValue()*1000+beginDate.getDayOfMonth()*100+roomNumber*10+beginTime);
    }

    public void updateRoomSchedule(int roomNumber, LocalDate beginDate, LocalDate endDate, int beginTime, int endTime){
        Room room = jdbcTemplateOrganisationDB.query("SELECT * FROM Room WHERE number=?",
                new Object[]{roomNumber}, new RoomMapper()).
                stream().findAny().orElse(null);
        assert room != null;
        Boolean[] tmpArray = room.getSchedule();
        //////////////////////////////////////////////////////////////

        if(beginDate.isEqual(endDate)) {
            for(int i=beginTime;i<=endTime;i++) {
                tmpArray[(int) (dayNumberRelativeToToday(beginDate)*hoursPerDay+i)] = true;
            }
        } else{
            long beginDiff = abs(dayNumberRelativeToToday(beginDate));
            long endDiff = abs(dayNumberRelativeToToday(endDate));

            for(int i=beginTime;i<hoursPerDay;i++){
                tmpArray[(int) (beginDiff*hoursPerDay+i)] = true;
            }
            for(int i=0;i<=endTime;i++){
                tmpArray[(int) (endDiff*hoursPerDay+i)] = true;
            }
        }

        ////////////////////////////////////
        room.setSchedule(tmpArray);
        jdbcTemplateOrganisationDB.update("UPDATE Room SET schedule=? WHERE number=?", room.getSchedule(), roomNumber);
    }

    public ArrayList<RoomScheduleForDay> getScheduleForDate(int dayNumber){
        List<Room> list = jdbcTemplateOrganisationDB.query("SELECT * FROM Room", new RoomMapper());
        ArrayList<RoomScheduleForDay> scheduleForDate = new ArrayList<>();
        for (Room room : list){
            scheduleForDate.add(getRoomScheduleForDayByRoom(room, dayNumber));
        }
        return scheduleForDate;
    }

    private RoomScheduleForDay getRoomScheduleForDayByRoom(Room room, int dayNumber){
        ArrayList<Boolean> arrayList = new ArrayList<>();
        for(int i=0;i<hoursPerDay;i++){
            arrayList.add(room.getSchedule()[(dayNumber-1)*hoursPerDay+i]);
        }
        return new RoomScheduleForDay(arrayList);
    }
}
