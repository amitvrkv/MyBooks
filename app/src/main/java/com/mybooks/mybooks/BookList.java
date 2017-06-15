package com.mybooks.mybooks;

/**
 * Created by am361000 on 12/06/17.
 */

public class BookList {
    private String key;
    private String title;
    private String author;
    private String sem;
    private String course;
    private String sellingprice;
    private String marketprice;
    private int avlcopy;

    public BookList(){

    }

    public BookList(String title, String author, String sem, String sellingprice, String marketprice, String course, int avlcopy, String key) {
        this.title = title;
        this.author = author;
        this.sem = sem;
        this.sellingprice = sellingprice;
        this.marketprice = marketprice;
        this.course = course;
        this.avlcopy = avlcopy;
        this.key = key;

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

    public String getSem() {
        return sem;

    }

    public void setSem(String sem) {
        this.sem = sem;
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

    public int getAvlcopy() { return avlcopy; }

    public void setAvlcopy(int avlcopy) {
        this.avlcopy = avlcopy;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
