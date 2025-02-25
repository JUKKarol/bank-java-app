package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String name;
    private String password;
    private int saldo;
    private String accountNumber;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", saldo=" + saldo +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }
}
