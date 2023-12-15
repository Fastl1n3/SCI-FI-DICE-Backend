package scifidice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ReservationRequest {

    private String dateStr;

    private int room;

    private String hours;

    private String userId;

    public ReservationRequest(String dateStr, int room, String hours, String userId) {
        this.dateStr = dateStr;
        this.room = room;
        this.hours = hours;
        this.userId = userId;
    }
}
