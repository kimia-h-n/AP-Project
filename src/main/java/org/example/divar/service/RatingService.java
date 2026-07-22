package org.example.divar.service;

public interface RatingService {

    void submitRating(long sellerId, int rating) throws RuntimeException;

    double getAverageRating(long sellerId) throws RuntimeException;
}
