package com.example.warrantycheckerforuser.model;

public class CustomerModel {
    String customerName,purchaseDate,expireDate;

    public CustomerModel( String customerName, String purchaseDate, String expireDate) {

        this.customerName = customerName;
        this.purchaseDate = purchaseDate;
        this.expireDate = expireDate;
    }

    public CustomerModel() {
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
