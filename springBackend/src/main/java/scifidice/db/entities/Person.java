package scifidice.db.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import static scifidice.config.SpringConfig.NSK_ZONE_ID;
@Getter
@Setter
@NoArgsConstructor
public class Person {
    private String phoneNumber;
    private boolean blackMark;
    private LocalDate lastVisit;
    private int discount;
    private String bookingBotChatID;
    private String infoBotChatID;

    public Person(String phoneNumber, String bookingBotChatID) {
        this.phoneNumber = phoneNumber;
        lastVisit=LocalDate.now(NSK_ZONE_ID);
        this.bookingBotChatID=bookingBotChatID;
    }
}
