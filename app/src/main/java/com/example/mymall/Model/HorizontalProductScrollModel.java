package com.example.mymall.Model;

public class HorizontalProductScrollModel {
    private String productId;
    private String productImage;
    private String productName;
    private String productDes;
    private String productPrice;

    public HorizontalProductScrollModel(String productId,String productImage, String productName, String productDes, String productPrice) {
        this.productId=productId;
        this.productImage = productImage;
        this.productName = productName;
        this.productDes = productDes;
        this.productPrice = productPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDes() {
        return productDes;
    }

    public void setProductDes(String productDes) {
        this.productDes = productDes;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
}
