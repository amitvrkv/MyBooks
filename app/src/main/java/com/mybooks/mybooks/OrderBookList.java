package com.mybooks.mybooks;

/**
 * Created by am361000 on 20/06/17.
 */

public class OrderBookList {
    String orderid;
    String date;
    String grandtotal;
    String status;
    String comment;

    public OrderBookList(){

    }

    public OrderBookList(String orderid, String date, String grandtotal, String status, String comment) {
        this.orderid = orderid;
        this.date = date;
        this.grandtotal = grandtotal;
        this.status = status;
        this.comment = comment;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGrandtotal() {
        return grandtotal;
    }

    public void setGrandtotal(String grandtotal) {
        this.grandtotal = grandtotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comment;
    }

    public void setComments(String comment) {
        this.comment = comment;
    }
}
