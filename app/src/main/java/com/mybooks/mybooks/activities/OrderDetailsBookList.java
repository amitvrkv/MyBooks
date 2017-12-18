package com.mybooks.mybooks.activities;

/**
 * Created by am361000 on 21/06/17.
 */

public class OrderDetailsBookList {
    String key;
    String booktype;
    String price;
    String quantity;

    public OrderDetailsBookList() {

    }

    public OrderDetailsBookList(String key, String booktype, String price, String quantity) {
        this.key = key;
        this.booktype = booktype;
        this.price = price;
        this.quantity = quantity;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBooktype() {
        return booktype;
    }

    public void setBooktype(String booktype) {
        this.booktype = booktype;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
