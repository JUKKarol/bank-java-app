package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
