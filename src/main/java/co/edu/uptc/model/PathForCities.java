package co.edu.uptc.model;

public class PathForCities {
    public City to;
    public double distance;
    public PathForCities(City to, double distance) {
        this.to = to;
        this.distance = distance;
    }
    public City getTo() {
        return to;
    }
    public double getDistance() {
        return distance;
    }
}
