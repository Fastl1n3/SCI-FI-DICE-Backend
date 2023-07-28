package scifidice.levachev.Model;

public class HoursPair {
    private int firstHour;
    private int secondHour;

    public HoursPair(int firstHour, int secondHour) {
        this.firstHour = firstHour;
        this.secondHour = secondHour;
    }


    public int getFirstHour() {
        return firstHour;
    }

    public void setFirstHour(int firstHour) {
        this.firstHour = firstHour;
    }

    public int getSecondHour() {
        return secondHour;
    }

    public void setSecondHour(int secondHour) {
        this.secondHour = secondHour;
    }
}
