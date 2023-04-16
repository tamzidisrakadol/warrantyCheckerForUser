package com.example.warrantycheckerforuser.model;

public class CustomerModel {
    int id;
    String purchaseDate,expireDate;

    public CustomerModel(int id, String purchaseDate, String expireDate) {
        this.id = id;
        this.purchaseDate = purchaseDate;
        this.expireDate = expireDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
}
