package com.example.melodify_app.Service;

public interface Service<T> {
    void save(T stuff);
    void getAll();
    void getById();
    void delete();
}
