package com.mybooks.mybooks;

/**
 * Created by am361000 on 12/06/17.
 */

public class BookList {
    private String title;
    private String author;
    private String cclass;
    private String course;
    private String sellingprice;
    private String marketprice;

    public BookList(){

    }

    public BookList(String title, String author, String cclass, String sellingprice, String marketprice, String course) {
        this.title = title;
        this.author = author;
        this.cclass = cclass;
        this.sellingprice = sellingprice;
        this.marketprice = marketprice;
        this.course = course;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCclass() {
        return cclass;

    }

    public void setCclass(String cclass) {
        this.cclass = cclass;
    }

    public String getSellingprice() {
        return sellingprice;
    }

    public void setSellingprice(String sellingprice) {
        this.sellingprice = sellingprice;
    }

    public String getMarketprice() {
        return marketprice;
    }

    public void setMarketprice(String marketprice) {
        this.marketprice = marketprice;
    }
}
