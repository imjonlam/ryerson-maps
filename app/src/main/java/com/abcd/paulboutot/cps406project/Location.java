package com.abcd.paulboutot.cps406project;

import java.util.Objects;

public class Location {
    /*
    Class representing locations
     */

    //Declaration of instance variables


    private String locationId;
    private String roomNumber;
    private String buildingCode;
    private String buildingName;
    private String address;
    private String coordinates;

    //Constructor

    //Empty constructor for Firebase
    public Location() {

    }

    //Use this constructor please
    public Location(String roomNumber, String buildingCode, String buildingName, String address, String coordinates) {
        this.locationId = buildingCode+roomNumber;
        this.roomNumber = roomNumber;
        this.buildingCode = buildingCode;
        this.buildingName = buildingName;
        this.address = address;
        this.coordinates = coordinates;
    }


    public Location(String locationId, String roomNumber, String buildingCode, String buildingName, String address, String coordinates) {
        this.locationId = locationId;
        this.roomNumber = roomNumber;
        this.buildingCode = buildingCode;
        this.buildingName = buildingName;
        this.address = address;
        this.coordinates = coordinates;
    }


    //Getters and Setters

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }


    //Equals and hashcode methods


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return roomNumber == location.roomNumber &&
                Objects.equals(buildingCode, location.buildingCode) &&
                Objects.equals(buildingName, location.buildingName) &&
                Objects.equals(address, location.address) &&
                Objects.equals(coordinates, location.coordinates);
    }

    @Override
    public int hashCode() {

        return Objects.hash(roomNumber, buildingCode, buildingName, address, coordinates);
    }

}
