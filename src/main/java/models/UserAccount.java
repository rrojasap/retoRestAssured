package models;

public class UserAccount {
    public String email;
    public String password;

    public UserAccount() {
        //empty constructor
    }

    public UserAccount(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
