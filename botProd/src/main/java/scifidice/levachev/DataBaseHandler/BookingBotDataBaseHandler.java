package scifidice.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.levachev.Mapper.PersonMapper;
import scifidice.levachev.Model.Booking;
import scifidice.levachev.Model.Person;
import scifidice.levachev.Model.Room;
import scifidice.levachev.Model.RoomScheduleForDay;
import scifidice.levachev.Mapper.RoomMapper;

import static java.lang.Math.abs;
import static java.time.temporal.ChronoUnit.DAYS;
import static scifidice.burym.config.SpringConfig.hoursPerDay;
import static scifidice.levachev.DataBaseHandler.AutoUpdatableDataBaseHandler.addToTodayBeginBookingList;
import static scifidice.levachev.DataBaseHandler.AutoUpdatableDataBaseHandler.addToTodayEndBookingList;

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
        return DAYS.between(LocalDate.now(), date);
    }

    public void authorization(String phoneNumber, String bookingBotChatID) throws DataAccessException {
        addPersonToTable(new Person(phoneNumber, bookingBotChatID), jdbcTemplateOrganisationDB);
    }

    public int book(String bookingChatID, LocalDate beginDate, LocalDate endDate, int beginTime, int endTime, int roomNumber){
        if(!isBookingValid(beginDate, endDate, beginTime, endTime, roomNumber)){
            return -1;
        }

        String phoneNumber = getPhoneNumberByBookingChatID(bookingChatID);
        if(phoneNumber == null){
            return -1;
        }

        int bookingNumber = generateUUID(roomNumber, beginTime, beginDate);

        Booking booking = new Booking(bookingNumber, phoneNumber, beginDate,
                endDate, beginTime, endTime, roomNumber);

        try{
            addBookingToTable(booking, jdbcTemplateOrganisationDB);
            if(booking.getEndDate().isEqual( LocalDate.now())){
                addToTodayEndBookingList(booking);
            }
            if(booking.getBeginDate().isEqual(LocalDate.now())){
                addToTodayBeginBookingList(booking);
            }
            updateRoomSchedule(booking.getRoomNumber(), booking.getBeginDate(), booking.getEndDate(), booking.getBeginTime(), booking.getEndTime());
        } catch (DataAccessException | AssertionError e){
            return -1;
        }
        return bookingNumber;
    }

    private boolean isBookingValid(LocalDate beginDate, LocalDate endDate, int beginTime, int endTime, int roomNumber){
        Room room = jdbcTemplateOrganisationDB.query("SELECT * FROM Room WHERE number=?",
                        new Object[]{roomNumber}, new RoomMapper()).
                stream().findAny().orElse(null);
        if(room == null){
            return false;
        }
        Boolean[] tmpArray = room.getSchedule();

        long beginDiff = abs(dayNumberRelativeToToday(beginDate));
        if(beginDate.isEqual(endDate)) {
            for(int i=beginTime;i<=endTime;i++) {
                if(tmpArray[(int)(beginDiff * hoursPerDay + i)]){
                    return false;
                }

            }
        } else{
            long endDiff = abs(dayNumberRelativeToToday(endDate));

            for(int i=beginTime;i<hoursPerDay;i++){
                if(tmpArray[(int) (beginDiff*hoursPerDay+i)]){
                    return false;
                }
            }
            for(int i=0;i<=endTime;i++){
                if(tmpArray[(int) (endDiff*hoursPerDay+i)]){
                    return false;
                }
            }
        }
        return true;
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
        for(int i=0;i<list.size();i++){
            scheduleForDate.add(null);
        }
        for (Room room : list){
            scheduleForDate.set(room.getNumber()-1, getRoomScheduleForDayByRoom(room, dayNumber));
        }
        return scheduleForDate;
    }

    private RoomScheduleForDay getRoomScheduleForDayByRoom(Room room, int dayNumber){
        ArrayList<Boolean> arrayList = new ArrayList<>();
        for(int i=0;i<hoursPerDay;i++){
            arrayList.add(room.getSchedule()[(dayNumber)*hoursPerDay+i]);
        }
        return new RoomScheduleForDay(arrayList);
    }
}
