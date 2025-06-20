package org.example;

public class CreateCourierRequest {

    private  String login;
    private  String password;
    private  String firstName;

    public CreateCourierRequest(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }
/*
    public CreateCourierRequest(String login, String firstName) {
        this.login = login;
        this.firstName = firstName;
    }
*/
    public String getLogin() { return login; }

    public String getPassword() { return password; }

    public String getFirstName() { return firstName; }

    public static void main(String[] args) { }
}
