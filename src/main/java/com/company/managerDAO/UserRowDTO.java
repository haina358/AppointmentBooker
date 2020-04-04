package com.company.managerDAO;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

/**
 * Data Transfer Object (DTO) that provides a mapping from each column in the SQL tables to instance variables.
 *
 * UserRowDTO is used as part of the DAO pattern to facilitate CRUD operations via AvailabilityManagerImpl and
 * BookingManagerImpl.
 * Each DTO represents a single row in its corresponding table (either bookings or openings).
 */
public class UserRowDTO {

    private String userName;
    private String companyName;
    private Timestamp startTime;
    private Timestamp endTime;

    public UserRowDTO(){
    }

    public UserRowDTO(String userName,String companyName, @NotNull Timestamp startTime, @NotNull Timestamp endTime) {
        this.userName = userName;
        this.companyName = companyName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public UserRowDTO(String userName, @NotNull Timestamp startTime, @NotNull Timestamp endTime) {
        this.userName = userName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getUserName() {
        return userName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setStartTime(@NotNull Timestamp startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(@NotNull Timestamp endTime) {
        this.endTime = endTime;
    }
}
