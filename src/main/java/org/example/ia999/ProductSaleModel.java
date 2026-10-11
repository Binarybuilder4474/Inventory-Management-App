package org.example.ia999;
public class ProductSaleModel {
    private String productName;
    private String productNumber;
    private double sellingPrice;
    private int quantitySold;

    public ProductSaleModel(String productName, String productNumber, double sellingPrice, int quantitySold) {
        this.productName = productName;
        this.productNumber = productNumber;
        this.sellingPrice = sellingPrice;
        this.quantitySold = quantitySold;
    }
    public String getProductName() {
        return productName;
    }
    public String getProductNumber() {
        return productNumber;
    }
    public double getSellingPrice() {
        return sellingPrice;
    }
    public int getQuantitySold() {
        return quantitySold;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }
    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }
}




