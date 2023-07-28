package scifidice.levachev.Model;

public class RoomInfo {
    private int number;
    private String password;
    private int currentPeopleNumber;
    private int firstHour;
    private int secondHour;


    public RoomInfo() {
    }

    public RoomInfo(int number, String password, int currentPeopleNumber, int firstHour, int secondHour) {
        this.number = number;
        this.password = password;
        this.currentPeopleNumber = currentPeopleNumber;
        this.firstHour = firstHour;
        this.secondHour = secondHour;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCurrentPeopleNumber() {
        return currentPeopleNumber;
    }

    public void setCurrentPeopleNumber(int currentPeopleNumber) {
        this.currentPeopleNumber = currentPeopleNumber;
    }

    public int getFirstHour() {
        return firstHour;
    }

    public void setFirstHour(int firstHour) {
        this.firstHour = firstHour;
    }

    public int getSecondHour() {
        return secondHour;
    }

    public void setSecondHour(int secondHour) {
        this.secondHour = secondHour;
    }
}
