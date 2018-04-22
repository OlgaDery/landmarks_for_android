package com.google.albertasights.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by olga on 4/16/18.
 */

public class User implements Serializable {

    @Expose
    private String id;

    @Expose
    private String email;

    private String password;

    @Expose
    private final Date regDate = new Date();

    @Expose
    private String role;

    @Expose
    private String firstName = "first name";

    @Expose
    private String lastName = "last name";

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    private Boolean loggedIn = true;

    private List<String> selectedPointsIds = new ArrayList<>();
    private List<String> createdPointsIds = new ArrayList<>();
    private List<String> selectedRoutesIds = new ArrayList<>();
    private List<String> createdRoutesIds = new ArrayList<>();
    private Boolean verified = false;

    public User (String mail, String uPassword) {
        this.email = mail;
        this.password = uPassword;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegDate() {
        return regDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getSelectedPointsIds() {
        return selectedPointsIds;
    }

    public void setSelectedPointsIds(List<String> selectedPointsIds) {
        this.selectedPointsIds = selectedPointsIds;
    }

    public List<String> getCreatedPointsIds() {
        return createdPointsIds;
    }

    public void setCreatedPointsIds(List<String> createdPointsIds) {
        this.createdPointsIds = createdPointsIds;
    }

    public List<String> getSelectedRoutesIds() {
        return selectedRoutesIds;
    }

    public void setSelectedRoutesIds(List<String> selectedRoutesIds) {
        this.selectedRoutesIds = selectedRoutesIds;
    }

    public List<String> getCreatedRoutesIds() {
        return createdRoutesIds;
    }

    public void setCreatedRoutesIds(List<String> createdRoutesIds) {
        this.createdRoutesIds = createdRoutesIds;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
