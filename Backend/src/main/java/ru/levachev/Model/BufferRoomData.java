package ru.levachev.Model;

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

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getPeopleNumber() {
        return peopleNumber;
    }

    public void setPeopleNumber(int peopleNumber) {
        this.peopleNumber = peopleNumber;
    }

    public boolean isShouldChange() {
        return isShouldChange;
    }

    public void setShouldChange(boolean shouldChange) {
        isShouldChange = shouldChange;
    }
}
