package scifidice.levachev.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BufferRoomData {
    private int roomNumber;
    private int peopleNumber;
    private boolean isShouldChange;

    public BufferRoomData() {
    }

    public BufferRoomData(int roomNumber) {
        this.roomNumber = roomNumber;
        this.peopleNumber = 0;
        this.isShouldChange = false;
    }
}
