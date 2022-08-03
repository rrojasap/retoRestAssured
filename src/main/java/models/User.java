package models;

public class User {

    public String name;
    public String email;
    public String password;
    public Integer age;

    public User(){
        //void constructor
    }

    public User(String name, String email, String password, Integer age) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
    }

}
