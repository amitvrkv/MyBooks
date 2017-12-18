package com.mybooks.mybooks.models;

/**
 * Created by am361000 on 20/06/17.
 */

public class ModelClassCustomOrder {

    String author;
    String date;
    String desc1;
    String desc2;
    String from;
    String orderid;
    String publisher;
    String status;
    String comment;
    String title;
    String getdetails;

    public ModelClassCustomOrder() {
    }

    public ModelClassCustomOrder(String author, String date, String desc1, String desc2, String from, String orderid, String publisher, String status, String comment, String title, String getdetails) {
        this.author = author;
        this.date = date;
        this.desc1 = desc1;
        this.desc2 = desc2;
        this.from = from;
        this.orderid = orderid;
        this.publisher = publisher;
        this.status = status;
        this.comment = comment;
        this.title = title;
        this.getdetails = getdetails;
    }

    public String getGetdetails() {
        return getdetails;
    }

    public void setGetdetails(String getdetails) {
        this.getdetails = getdetails;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesc1() {
        return desc1;
    }

    public void setDesc1(String desc1) {
        this.desc1 = desc1;
    }

    public String getDesc2() {
        return desc2;
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
