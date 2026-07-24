package org.example.divar.service;

/**
 * Interface for managing seller ratings and retrieving average score.
 */
public interface RatingService {

    void submitRating(long sellerId, int rating) throws RuntimeException;

    double getAverageRating(long sellerId) throws RuntimeException;
}
