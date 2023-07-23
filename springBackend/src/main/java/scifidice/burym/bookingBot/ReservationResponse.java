package scifidice.burym.bookingBot;

public class ReservationResponse {
    private int codeAnswer;
    private String bookId;
    public ReservationResponse() {}

    public ReservationResponse(int codeAnswer, String bookId) {
        this.codeAnswer = codeAnswer;
        this.bookId = bookId;

    }

    public void setCodeAnswer(int codeAnswer) {
        this.codeAnswer = codeAnswer;
    }

    public int getCodeAnswer() {
        return codeAnswer;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookId() {
        return bookId;
    }
}
