package org.example;

import org.junit.jupiter.api.BeforeEach;

public class CreateCourierRequest {

    private  String login;
    private  String password;
    private  String firstName;

    public CreateCourierRequest(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    public String getLogin() { return login; }

    public String getPassword() { return password; }

    public String getFirstName() { return firstName; }

    public void setLogin(String login) { this.login = login; }

    public void setPassword(String password) { this.password = password; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public static void main(String[] args) { }
}
