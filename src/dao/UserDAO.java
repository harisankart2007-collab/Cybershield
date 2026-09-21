package dao;

import model.User;

public class UserDAO {

    // saves a new user in the database
    public boolean register(User u) {
        return false;   // real SQL comes after DBConnection is ready
    }

    // returns the user if email and password are correct, otherwise null
    public User login(String email, String password) {
        return null;    // real SQL comes after DBConnection is ready
    }
}