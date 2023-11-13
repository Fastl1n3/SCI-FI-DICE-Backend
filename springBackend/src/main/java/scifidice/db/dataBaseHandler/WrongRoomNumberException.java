package scifidice.db.dataBaseHandler;

public class WrongRoomNumberException extends Exception {
    WrongRoomNumberException(String message) {
        super(message);
    }
}
