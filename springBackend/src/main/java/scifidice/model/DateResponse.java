package scifidice.model;

public class DateResponse {
    private int codeAnswer;
    private String dateStr;

    public DateResponse() {}

    public DateResponse(int codeAnswer, String date) {
        this.codeAnswer = codeAnswer;
        this.dateStr = date;
    }

    public int getCodeAnswer() {
        return codeAnswer;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setCodeAnswer(int codeAnswer) {
        this.codeAnswer = codeAnswer;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
}
