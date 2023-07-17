package ru.levachev;

import ru.levachev.DataBaseHandler.*;
import ru.levachev.Model.Booking;

import java.io.IOException;
import java.time.LocalDate;

public class Launcher {
    public static void main(String[] args) throws IOException {
        BookingBotDataBaseHandler bookingBotDataBaseHandler =
                new BookingBotDataBaseHandler(new Config().jdbcTemplateOrganisationDB());
        System.out.println(bookingBotDataBaseHandler.authorization("892", "213"));
        bookingBotDataBaseHandler.book("213"
                , LocalDate.now(), LocalDate.now(),
                10, 11, 2
        );
    }
}
