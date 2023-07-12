package ru.levachev.Model;

import java.time.LocalDate;

public class Person {
    private String phoneNumber;
    private boolean blackMark;
    private LocalDate lastVisit;
    private int discount;

    public Person() {
    }
    public Person(String phoneNumber, LocalDate lastVisit) {
        this.phoneNumber = phoneNumber;
        blackMark = false;
        this.lastVisit = lastVisit;
        discount = 0;
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
}
