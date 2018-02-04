package com.example.joanabeleza.hobbytv.Models;

/**
 * Project HobbyTV refactored by joanabeleza on 20/05/2017.
 */

public class Review {
    private String author;
    private String review;

    public Review(String author, String review) {
        this.author = author;
        this.review = review;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
