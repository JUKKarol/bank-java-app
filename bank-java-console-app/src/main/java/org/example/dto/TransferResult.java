package org.example.dto;

import org.example.model.User;

import java.util.ArrayList;

public class TransferResult {
    private boolean isSuccess;
    private ArrayList<User> users;


    public TransferResult(ArrayList<User> users, boolean isSuccess) {
        this.isSuccess = isSuccess;
        this.users = users;
    }

    public TransferResult() {
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}
