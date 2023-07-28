package scifidice.burym.model;

public class ReservationRequest {
    private String dateStr;
    private int room;
    private String hours;
    private String userId;
    public ReservationRequest() {}

    public ReservationRequest(String dateStr, int room, String hours, String userId) {
        this.dateStr = dateStr;
        this.room = room;
        this.hours = hours;
        this.userId = userId;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getDateStr() {
        return dateStr;
    }

    public int getRoom() {
        return room;
    }

    public String getHours() {
        return hours;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
