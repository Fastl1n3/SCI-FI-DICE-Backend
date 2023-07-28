package scifidice.burym.model;

public class AdminMessage {
    
    AdminMessageType messageType;
    
    String message;

    public AdminMessage() {
    }

    public AdminMessage(AdminMessageType messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }

    public AdminMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(AdminMessageType messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
