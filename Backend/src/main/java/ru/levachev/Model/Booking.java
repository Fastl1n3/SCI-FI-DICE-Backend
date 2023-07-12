package ru.levachev.Model;

import java.sql.Time;
import java.sql.Date;

public class Booking {
    private int bookingNumber;
    private int date;
    private int beginTime;
    private int endTime;
    private String phoneNumber;
    private int roomNumber;
    private int gameID;

    public Booking(){
    }

    public Booking(String phoneNumber, int date, int beginTime, int endTime, int roomNumber) {
        this.date = date;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.phoneNumber = phoneNumber;
        this.bookingNumber = -1;
        this.roomNumber = roomNumber;
        this.gameID = -1;
    }


    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(int beginTime) {
        this.beginTime = beginTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(int bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
}
