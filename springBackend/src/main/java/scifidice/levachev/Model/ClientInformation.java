package scifidice.levachev.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
