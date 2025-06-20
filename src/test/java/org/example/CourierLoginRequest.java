package org.example;

public class CourierLoginRequest {

    private final String login;
    private final String password;

    public CourierLoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() { return login; }
    public String getPassword() { return password; }

    public static void main(String[] args) {
    }
}
