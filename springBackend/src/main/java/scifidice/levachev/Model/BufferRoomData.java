package scifidice.levachev.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BufferRoomData {
    private int roomNumber;
    private int peopleNumber;
    private boolean isShouldChange;

    public BufferRoomData(int roomNumber) {
        this.roomNumber = roomNumber;
        this.peopleNumber = 0;
        this.isShouldChange = false;
    }
}
