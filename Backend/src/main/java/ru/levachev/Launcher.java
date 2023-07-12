package ru.levachev;

import ru.levachev.DataBaseHandler.*;
import ru.levachev.Model.Booking;
import ru.levachev.Model.ClientInformation;

public class Launcher {
    public static void main(String[] args) {
        AdministratorDataBaseHandler administratorDataBaseHandler =
                new AdministratorDataBaseHandler(new Config().jdbcTemplateOrganisationDB(),
                        new Config().jdbcTemplateGamesDB());

        AutoUpdatableDataBaseHandler.initTodayBookingList();

        /*BookingBotDataBaseHandler bookingBotDataBaseHandler =
                new BookingBotDataBaseHandler(new Config().jdbcTemplateOrganisationDB());
        bookingBotDataBaseHandler.book(
                new Booking("+44", 1, 0, 2, 1)
        );*/

        ReceptionDataBaseHandler receptionDataBaseHandler =
                new ReceptionDataBaseHandler(
                        new Config().jdbcTemplateOrganisationDB()
                );
        System.out.println(receptionDataBaseHandler.isBookingNumberValid(10));
        ClientInformation clientInformation =
                receptionDataBaseHandler.payBooking(2, 10);
        System.out.println(clientInformation.getPassword());
        System.out.println(clientInformation.getRoomNumber());
        System.out.println(clientInformation.getBeginTime());
        System.out.println(clientInformation.getEndTime());
    }
}
