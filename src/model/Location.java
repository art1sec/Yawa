package model;

public class Location {

    public String name;
    public String country;
    public String state;
    public double lat;
    public double lon;

    @Override
    public String toString() {
        return name+", "+country+((state != null)?(", "+state):"");
    }

}
