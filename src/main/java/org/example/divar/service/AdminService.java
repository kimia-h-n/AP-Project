package org.example.divar.service;

import org.example.divar.model.Advertisement;

import java.util.ArrayList;

public interface AdminService {

    ArrayList<Advertisement> getPendingAdvertisements() throws RuntimeException;

    void approveAdvertisement(long adId) throws RuntimeException;

    void rejectAdvertisement(long adId, String rejectReason) throws RuntimeException;
}
