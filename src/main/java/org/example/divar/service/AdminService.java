package org.example.divar.service;

import org.example.divar.model.Advertisement;
import org.example.divar.model.AdminReport;
import org.example.divar.model.ReportResolutionAction;
import org.example.divar.model.User;

import java.util.ArrayList;

public interface AdminService {

    ArrayList<Advertisement> getPendingAdvertisements() throws RuntimeException;

    void approveAdvertisement(long adId) throws RuntimeException;

    void rejectAdvertisement(long adId, String rejectReason) throws RuntimeException;

    ArrayList<User> getAllUsers() throws RuntimeException;

    User getUserDetails(long userId) throws RuntimeException;

    void blockUser(String id, String reason) throws RuntimeException;

    void unblockUser(String id) throws RuntimeException;

    ArrayList<AdminReport> getReports() throws RuntimeException;

    void resolveReport(long reportId, ReportResolutionAction action, String note) throws RuntimeException;

    ArrayList<Advertisement> getUserAdvertisements(long userId) throws RuntimeException;
}

