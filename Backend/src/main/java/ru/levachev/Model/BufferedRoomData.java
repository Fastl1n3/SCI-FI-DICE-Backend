package ru.levachev.Model;

public class BufferedRoomData {
    private int roomNumber;
    private int peopleNumber;
    private boolean isShouldChange;

    public BufferedRoomData() {
    }

    public BufferedRoomData(int roomNumber, int peopleNumber) {
        this.roomNumber = roomNumber;
        this.peopleNumber = peopleNumber;
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
