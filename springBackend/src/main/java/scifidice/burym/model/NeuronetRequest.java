package scifidice.burym.model;

public class NeuronetRequest {
    private int room;
    private int people;
    private String time;
    public NeuronetRequest() {}
    public NeuronetRequest(int room, int people, String time) {
        this.room = room;
        this.people = people;
        this.time = time;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getRoom() {
        return room;
    }

    public int getPeople() {
        return people;
    }

    public String getTime() {
        return time;
    }
}
