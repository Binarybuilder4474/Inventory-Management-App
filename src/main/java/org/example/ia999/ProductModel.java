package org.example.ia999;

public class ProductModel {
    private Integer id;
    private String productName;
    private String productNumber;
    private String supplierContact;
    private Double initialPrice;
    private Integer quantity;


    public ProductModel(Integer id, String productName, String productNumber, String supplierContact, Double initialPrice, Integer quantity) {
        this.id = id;
        this.productName = productName;
        this.productNumber = productNumber;
        this.supplierContact = supplierContact;
        this.initialPrice = initialPrice;
        this.quantity = quantity;
    }
    public Integer getId() {
        return id;
    }
    public String getProductName() {
        return productName;
    }
    public String getProductNumber() {
        return productNumber;
    }
    public String getSupplierContact() {
        return supplierContact;
    }
    public Double getInitialPrice() {
        return initialPrice;
    }
    public Integer getQuantity() {
        return quantity;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }
    public void setSupplierContact(String supplierContact) {
        this.supplierContact = supplierContact;
    }
    public void setInitialPrice(Double initialPrice) {
        this.initialPrice = initialPrice;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}






