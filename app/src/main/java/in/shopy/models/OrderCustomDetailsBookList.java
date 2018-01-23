package in.shopy.models;

/**
 * Created by am361000 on 02/01/18.
 */

public class OrderCustomDetailsBookList {
    String author;
    String bookType;
    String course;
    String description;
    String estPrice;
    String key;
    String mrp;
    String publisher;
    String qty;
    String title;
    String total;

    public OrderCustomDetailsBookList() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public OrderCustomDetailsBookList(String author, String bookType, String course, String description, String estPrice, String key, String mrp, String publisher, String qty, String title, String total) {

        this.author = author;
        this.bookType = bookType;
        this.course = course;
        this.description = description;
        this.estPrice = estPrice;
        this.mrp = mrp;
        this.publisher = publisher;
        this.qty = qty;
        this.title = title;
        this.total = total;
        this.key = key;

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEstPrice() {
        return estPrice;
    }

    public void setEstPrice(String estPrice) {
        this.estPrice = estPrice;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
