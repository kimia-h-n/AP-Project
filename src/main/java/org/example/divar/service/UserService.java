package org.example.divar.service;

public interface UserService {

    void login(String username, String password) throws RuntimeException;

    void register(String firstname, String lastname, String username, String password, String phoneNumber, String email) throws RuntimeException;

    String getNameByUsername(String username);

}


