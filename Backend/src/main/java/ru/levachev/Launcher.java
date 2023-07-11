package ru.levachev;

import ru.levachev.DataBaseHandler.BookingBotDataBaseHandler;
import ru.levachev.Model.Booking;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Launcher {
    public static void main(String[] args) {

        /*BookingBotDataBaseHandler bookingBotDataBaseHandler =
                new BookingBotDataBaseHandler(new Config().jdbcTemplateOrganisationDB());
        bookingBotDataBaseHandler.setDefaultSchedule();
        bookingBotDataBaseHandler.book(new Booking("899",
                1, 0, 3, 1));*/

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("GMT+7"));
        System.out.println(localDateTime.getMinute());

        /*bookingBotDataBaseHandler.truncateTable("Room");
        bookingBotDataBaseHandler.setDefaultSchedule();
        bookingBotDataBaseHandler.truncateTable("Booking");
        Booking booking = new Booking("8923", 1, 0, 3, 3);
        bookingBotDataBaseHandler.book(booking);*/

        //bookingBotDataBaseHandler.updateRoomSchedule(1, 1, 0, 5);
    }
}
