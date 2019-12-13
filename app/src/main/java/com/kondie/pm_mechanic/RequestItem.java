package com.kondie.pm_mechanic;

public class RequestItem {

    String imagePath, makeAndModel, clientFName, status, requestName, clientEmail, clientLName, clientPhone, requestComment, issue, requestAmount, serviceFee, dateCreated, id;
    double lat, lng, shopLat, shopLng;

    public RequestItem(){}

    public RequestItem(String imagePath, String clientFName, String clientEmail, String clientLName, String clientPhone, double lat, double lng){
        this.imagePath = imagePath;
        this.clientFName = clientFName;
        this.clientEmail = clientEmail;
        this.clientLName = clientLName;
        this.clientPhone = clientPhone;
    }

    public void setMakeAndModel(String makeAndModel) {
        this.makeAndModel = makeAndModel;
    }

    public String getMakeAndModel() {
        return makeAndModel;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(String serviceFee) {
        this.serviceFee = serviceFee;
    }

    public void setShopLng(double shopLng) {
        this.shopLng = shopLng;
    }

    public void setShopLat(double shopLat) {
        this.shopLat = shopLat;
    }

    public double getShopLat() {
        return shopLat;
    }

    public double getShopLng() {
        return shopLng;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setRequestAmount(String requestAmount) {
        this.requestAmount = requestAmount;
    }

    public void setRequestComment(String requestComment) {
        this.requestComment = requestComment;
    }

    public String getRequestAmount() {
        return requestAmount;
    }

    public String getRequestComment() {
        return requestComment;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public String getClientFName() {
        return clientFName;
    }

    public String getClientLName() {
        return clientLName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public void setClientFName(String clientFName) {
        this.clientFName = clientFName;
    }

    public void setClientLName(String clientLName) {
        this.clientLName = clientLName;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
