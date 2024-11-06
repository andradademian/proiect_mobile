package com.example.melodify_app;
import org.mindrot.jbcrypt.BCrypt;

public class User {

    String name;
    String email;
    String password;
    String birthday;
    public User(String name, String email, String password, String birthday) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

//    public String hashPassword(String password) {
//        return BCrypt.hashpw(getPassword(), BCrypt.gensalt());
//    }
//
//    public static boolean checkPassword(String password, String hashedPassword) {
//        return BCrypt.checkpw(password, hashedPassword);
//    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
