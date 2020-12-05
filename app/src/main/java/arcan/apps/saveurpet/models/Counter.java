package arcan.apps.saveurpet.models;

public class Counter {
    String type;
    String date;
    String municity;

    public Counter() { }

    public Counter(String type, String date, String municity) {
        this.type = type;
        this.date = date;
        this.municity = municity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMunicity() {
        return municity;
    }

    public void setMunicity(String municity) {
        this.municity = municity;
    }

}
