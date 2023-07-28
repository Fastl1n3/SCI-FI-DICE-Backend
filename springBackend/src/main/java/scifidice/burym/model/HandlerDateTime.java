package scifidice.burym.model;

import scifidice.levachev.Model.HoursPair;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandlerDateTime {

    public static StringBuilder hoursPairsHandler(ArrayList<HoursPair> hoursPairs) {
        StringBuilder stringBuilder = new StringBuilder();
        for (HoursPair hoursPair: hoursPairs) {
            stringBuilder.append(hoursPair.getFirstHour()).append("-").append(hoursPair.getSecondHour()).append(", ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 2);
        stringBuilder.append("\n");
        return stringBuilder;
    }

    public static LocalDate getDateObject(String dateStr) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ROOT);
        LocalDate date = LocalDate.parse(dateStr, formatter);
        if (date.isBefore(LocalDate.now())) {
            throw new DateTimeParseException("Data is before now date", dateStr, 1);
        }
        if (Integer.parseInt(dateStr.split("\\.")[0]) != date.getDayOfMonth()) {
            throw new DateTimeParseException("Not valid Date", dateStr, 1);
        }
        return date;
    }

    public static int[] getHours(String hoursStr) throws RuntimeException {
        Pattern twopart = Pattern.compile("(\\d{1,2})-(\\d{1,2})");
        Matcher m = twopart.matcher(hoursStr);
        if (!m.matches()) {
            throw new RuntimeException("Invalid hours");
        }
        int[] intHours = new int[]{Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))};
        if (intHours[0] < 0 || intHours[0] > 23 || intHours[1] <= intHours[0] || intHours[1] > 24) {
            throw new RuntimeException("Invalid hours");
        }
        return intHours;
    }
}
