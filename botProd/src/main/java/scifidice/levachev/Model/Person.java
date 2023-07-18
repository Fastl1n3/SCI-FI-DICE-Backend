package scifidice.levachev.Model;

import java.time.LocalDate;

public class Person {
    private String phoneNumber;
    private boolean blackMark;
    private LocalDate lastVisit;
    private int discount;
    private String bookingBotChatID;
    private String infoBotChatID;

    public Person() {
    }
    public Person(String phoneNumber, String bookingBotChatID) {
        this.phoneNumber = phoneNumber;
        lastVisit=LocalDate.now();
        blackMark = false;
        discount = 0;
        this.bookingBotChatID=bookingBotChatID;
        infoBotChatID = null;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isBlackMark() {
        return blackMark;
    }

    public void setBlackMark(boolean blackMark) {
        this.blackMark = blackMark;
    }

    public LocalDate getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(LocalDate lastVisit) {
        this.lastVisit = lastVisit;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getBookingBotChatID() {
        return bookingBotChatID;
    }

    public void setBookingBotChatID(String bookingBotChatID) {
        this.bookingBotChatID = bookingBotChatID;
    }

    public String getInfoBotChatID() {
        return infoBotChatID;
    }

    public void setInfoBotChatID(String infoBotChatID) {
        this.infoBotChatID = infoBotChatID;
    }
}
