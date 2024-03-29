package scifidice.db.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
public class Booking {
    private int bookingNumber;
    private LocalDate beginDate;
    private LocalDate endDate;
    private int beginTime;
    private int endTime;
    private String phoneNumber;
    private int roomNumber;
    private int currentPeopleNumber;
    private boolean isPaid;

    public Booking(int bookingNumber, String phoneNumber, LocalDate beginDate, LocalDate endDate, int beginTime, int endTime, int roomNumber, boolean isPaid) {
        this.bookingNumber = bookingNumber;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.phoneNumber = phoneNumber;
        this.roomNumber = roomNumber;
        currentPeopleNumber = 0;
        this.isPaid = isPaid;
    }
}
