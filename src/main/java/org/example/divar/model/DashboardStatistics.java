package org.example.divar.model;

public class DashboardStatistics {
    private Integer numActiveUsers;
    private Integer numBlockedUsers;
    private Integer numAds;
    private Integer numPendingAds;
    private Integer numReports;

    public Integer getNumActiveUsers() {
        return numActiveUsers; }

    public Integer getNumBlockedUsers() {
        return numBlockedUsers; }

    public Integer getNumAds() {
        return numAds; }

    public Integer getNumPendingAds() {
        return numPendingAds; }

    public Integer getNumReports() {
        return numReports; }

    public void setNumActiveUsers(Integer numActiveUsers) {
        this.numActiveUsers = numActiveUsers; }

    public void setNumBlockedUsers(Integer numBlockedUsers) {
        this.numBlockedUsers = numBlockedUsers; }

    public void setNumAds(Integer numAds) {
        this.numAds = numAds; }

    public void setNumPendingAds(Integer numPendingAds) {
        this.numPendingAds = numPendingAds; }

    public void setNumReports(Integer numReports) {
        this.numReports = numReports; }
}