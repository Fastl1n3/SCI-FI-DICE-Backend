package scifidice.levachev.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckPeopleInformation {
    private String infoBotChatID;
    private boolean inExitWindow;
    private boolean isViolate;
    private String phoneNumber;

    public CheckPeopleInformation(String infoBotChatID, boolean inExitWindow, boolean isViolate, String phoneNumber) {
        this.infoBotChatID = infoBotChatID;
        this.inExitWindow = inExitWindow;
        this.isViolate=isViolate;
        this.phoneNumber=phoneNumber;
    }

    public CheckPeopleInformation(){
    }
}
