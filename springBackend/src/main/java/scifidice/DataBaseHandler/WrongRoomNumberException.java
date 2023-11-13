package scifidice.DataBaseHandler;

public class WrongRoomNumberException extends Exception {
    WrongRoomNumberException(String message) {
        super(message);
    }
}
