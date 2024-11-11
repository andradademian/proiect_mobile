package com.example.melodify_app.Service;

import com.example.melodify_app.Model_Auxiliare.User;

public interface Service<T> {
    void save(T stuff);
    void getAll();
    void getById();
    User getUserByEmail(String email);
    void delete();
}
