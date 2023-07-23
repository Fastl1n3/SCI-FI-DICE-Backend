package scifidice.levachev.Model;

public class ClientInformation {
    private int roomNumber;
    private int beginTime;
    private int endTime;
    private String password;
    private ReceptionCodeAnswer codeAnswer;

    public ClientInformation(){
    }

    public ClientInformation(ReceptionCodeAnswer codeAnswer, int roomNumber, int beginTime, int endTime, String password) {
        this.roomNumber = roomNumber;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.password = password;
        this.codeAnswer=codeAnswer;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(int beginTime) {
        this.beginTime = beginTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public ReceptionCodeAnswer getCodeAnswer() {
        return codeAnswer;
    }

    public void setCodeAnswer(ReceptionCodeAnswer codeAnswer) {
        this.codeAnswer = codeAnswer;
    }
}
