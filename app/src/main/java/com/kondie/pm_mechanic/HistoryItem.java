package com.kondie.pm_mechanic;

public class HistoryItem {

    String clientName, minServiceFee, dateCreated, id, status, issue, car, comment;
    double lat, lng;

    public HistoryItem(){}

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getCar() {
        return car;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setMinServiceFee(String minServiceFee) {
        this.minServiceFee = minServiceFee;
    }

    public String getClientName() {
        return clientName;
    }

    public String getIssue() {
        return issue;
    }

    public String getStatus() {
        return status;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getMinServiceFee() {
        return minServiceFee;
    }

    public String getId() {
        return id;
    }
}
