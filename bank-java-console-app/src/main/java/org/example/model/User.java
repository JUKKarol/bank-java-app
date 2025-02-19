package org.example.model;

public class User {
    private String name;
    private String password;
    private int saldo;
    private String accountNumber;

    public User(String name, String password, int saldo, String accountNumber) {
        this.name = name;
        this.password = password;
        this.saldo = saldo;
        this.accountNumber = accountNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", saldo=" + saldo +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }
}
