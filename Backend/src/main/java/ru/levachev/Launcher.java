package ru.levachev;

import ru.levachev.DataBaseHandler.*;
import ru.levachev.Model.Booking;
import ru.levachev.Model.ClientInformation;

import java.io.IOException;
import java.time.LocalDate;

public class Launcher {
    public static void main(String[] args) throws IOException {
        /*AdministratorDataBaseHandler administratorDataBaseHandler =
                new AdministratorDataBaseHandler(new Config().jdbcTemplateOrganisationDB(),
                        new Config().jdbcTemplateGamesDB());*/

        AutoUpdatableDataBaseHandler.initTodayBeginBookingList();
        AutoUpdatableDataBaseHandler.initTodayEndBookingList();


        ReceptionDataBaseHandler receptionDataBaseHandler =
                new ReceptionDataBaseHandler(new Config().jdbcTemplateOrganisationDB(),
                new Config().jdbcTemplateGamesDB());
        System.out.println(receptionDataBaseHandler.isBookingNumberValid(11));
    }
}
