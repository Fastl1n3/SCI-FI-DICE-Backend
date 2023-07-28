package scifidice.burym.model;

public class PhoneRequest {
    private String phone;
    private String bookingChatId;

    public PhoneRequest() {
    }

    public PhoneRequest(String phone, String bookingChatId) {
        this.phone = phone;
        this.bookingChatId = bookingChatId;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBookingChatId(String bookingChatId) {
        this.bookingChatId = bookingChatId;
    }

    public String getPhone() {
        return phone;
    }

    public String getBookingChatId() {
        return bookingChatId;
    }
}
