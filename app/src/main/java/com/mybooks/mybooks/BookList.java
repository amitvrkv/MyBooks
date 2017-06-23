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
    private String priceOld;
    private String priceMRP;
    private String priceNew;
    private int avlcopy;
    private String src;

    public BookList(){

    }

    public BookList(String title, String author, String sem, String priceOld, String priceMRP, String priceNew,String course, int avlcopy, String key, String src) {
        this.title = title;
        this.author = author;
        this.sem = sem;
        this.priceOld = priceOld;
        this.priceMRP = priceMRP;
        this.course = course;
        this.avlcopy = avlcopy;
        this.key = key;
        this.priceNew = priceNew;
        this.src = src;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
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

    public String getPriceOld() {
        return priceOld;
    }

    public void setPriceOld(String priceOld) {
        this.priceOld = priceOld;
    }

    public String getPriceMRP() {
        return priceMRP;
    }

    public void setPriceMRP(String priceMRP) {
        this.priceMRP = priceMRP;
    }

    public String getPriceNew() {
        return priceNew;
    }

    public void setPriceNew(String priceNew) {
        this.priceNew = priceNew;
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
