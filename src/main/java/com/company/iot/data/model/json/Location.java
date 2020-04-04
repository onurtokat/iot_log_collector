package com.company.iot.data.model.json;

public class Location {

    private Long latitude;
    private Long longitude;

    public Location(Long latitude, Long longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getLatitude() {
        return latitude;
    }

    public Long getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
