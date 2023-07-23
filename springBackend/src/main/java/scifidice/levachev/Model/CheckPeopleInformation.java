package scifidice.levachev.Model;

public class CheckPeopleInformation {
    private String infoBotChatID;
    private boolean inExitWindow;
    private boolean isViolate;

    public CheckPeopleInformation(String infoBotChatID, boolean inExitWindow, boolean isViolate) {
        this.infoBotChatID = infoBotChatID;
        this.inExitWindow = inExitWindow;
        this.isViolate=isViolate;
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
}
