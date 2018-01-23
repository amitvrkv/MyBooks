package in.shopy.models;

/**
 * Created by am361000 on 03/10/17.
 */

public class ModelClassBLog {

    String by;
    String comment;
    String dislike;
    String dislikeFrom;
    String like;
    String likeFrom;
    String email;
    String id;
    String date;

    public ModelClassBLog() {
    }

    public ModelClassBLog(String by, String comment, String dislike, String dislikeFrom, String like, String likeFrom, String email, String id, String date) {
        this.by = by;
        this.comment = comment;
        this.dislike = dislike;
        this.dislikeFrom = dislikeFrom;
        this.like = like;
        this.likeFrom = likeFrom;
        this.email = email;
        this.id = id;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDislike() {
        return dislike;
    }

    public void setDislike(String dislike) {
        this.dislike = dislike;
    }

    public String getDislikeFrom() {
        return dislikeFrom;
    }

    public void setDislikeFrom(String dislikeFrom) {
        this.dislikeFrom = dislikeFrom;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getLikeFrom() {
        return likeFrom;
    }

    public void setLikeFrom(String likeFrom) {
        this.likeFrom = likeFrom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
