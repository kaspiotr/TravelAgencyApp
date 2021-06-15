package pl.edu.agh.travelagencyapp.model;

public class ExtendedTrip {
    private Trip trip;
    private int availablePlaces;

    public ExtendedTrip(Trip trip){
        this.trip = new Trip(trip);
        this.availablePlaces = trip.countAvailablePlaces();
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public int getAvailablePlaces() {
        return availablePlaces;
    }

    public void setAvailablePlaces(int availablePlaces) {
        this.availablePlaces = availablePlaces;
    }
}
