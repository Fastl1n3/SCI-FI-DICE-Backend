package scifidice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomInfo {
    private int number;
    private String password;
    private int currentPeopleNumber;
    private int firstHour;
    private int secondHour;
}
