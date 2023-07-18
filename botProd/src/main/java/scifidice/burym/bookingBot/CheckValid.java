package scifidice.burym.bookingBot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckValid {
    public static LocalDate getDateObject(String dateStr) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ROOT);
        LocalDate date = LocalDate.parse(dateStr, formatter);
        if (date.isBefore(LocalDate.now())) {
            throw new DateTimeParseException("Data is before now date", dateStr, 1);
        }
        if (Integer.parseInt(dateStr.split("\\.")[0]) != date.getDayOfMonth()) {
            throw new DateTimeParseException("Not valid Date", dateStr, 1);
        }
        System.out.println(date);
        return date;
    }
    public static int[] checkHours(String hoursStr) throws Exception {
        Pattern twopart = Pattern.compile("(\\d{1,2})-(\\d{1,2})");
        Matcher m = twopart.matcher(hoursStr);
        if (!m.matches()) {
            throw new RuntimeException("Invalid hours");
        }
        int[] intHours = new int[]{Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))};
        if (intHours[0] < 0 || intHours[0] > 23 || intHours[1] <= intHours[0] || intHours[1] > 23) {
            throw new RuntimeException("Invalid hours");
        }

        System.out.println(m.group(1) + " " + m.group(2));
        return intHours;
    }
}
