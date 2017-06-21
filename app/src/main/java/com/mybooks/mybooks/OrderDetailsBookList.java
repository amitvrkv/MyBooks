package com.mybooks.mybooks;

/**
 * Created by am361000 on 21/06/17.
 */

public class OrderDetailsBookList {
    String author;
    String bookkey;
    String booktype;
    String course;
    String price;
    String quantity;
    String sem;
    String title;

    public OrderDetailsBookList() {

    }

    public OrderDetailsBookList(String author, String bookkey, String booktype, String course, String price, String quantity, String sem, String title) {
        this.author = author;
        this.bookkey = bookkey;
        this.booktype = booktype;
        this.course = course;
        this.price = price;
        this.quantity = quantity;
        this.sem = sem;
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBookkey() {
        return bookkey;
    }

    public void setBookkey(String bookkey) {
        this.bookkey = bookkey;
    }

    public String getBooktype() {
        return booktype;
    }

    public void setBooktype(String booktype) {
        this.booktype = booktype;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
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

    public String getSem() {
        return sem;
    }

    public void setSem(String sem) {
        this.sem = sem;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
