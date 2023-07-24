package scifidice.levachev.Model;

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

    public String getInfoBotChatID() {
        return infoBotChatID;
    }

    public void setInfoBotChatID(String infoBotChatID) {
        this.infoBotChatID = infoBotChatID;
    }

    public boolean isInExitWindow() {
        return inExitWindow;
    }

    public void setInExitWindow(boolean inExitWindow) {
        this.inExitWindow = inExitWindow;
    }

    public boolean isViolate() {
        return isViolate;
    }

    public void setViolate(boolean violate) {
        isViolate = violate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
