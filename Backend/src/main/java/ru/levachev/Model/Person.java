package ru.levachev.Model;

public class Person {
    private String phoneNumber;
    private boolean blackMark;

    public Person(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        blackMark = false;
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
}
