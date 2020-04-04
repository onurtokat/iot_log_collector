package com.company.iot.data.model.json;

/**
 * Data class is the POJO for JSON raw data
 * @author Onur Tokat
 */
public class Data {
    private String deviceId;
    private int temperature;
    private Location location;
    private long time;

    public Data(String deviceId, int temperature, Location location, long time) {
        this.deviceId = deviceId;
        this.temperature = temperature;
        this.location = location;
        this.time = time;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public int getTemperature() {
        return temperature;
    }

    public Location getLocation() {
        return location;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Data{" +
                "deviceId='" + deviceId + '\'' +
                ", temperature=" + temperature +
                ", location=" + location +
                ", time=" + time +
                '}';
    }
}
