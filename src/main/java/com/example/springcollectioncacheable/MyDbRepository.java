package com.example.springcollectioncacheable;

import org.springframework.stereotype.Component;

@Component
public class MyDbRepository {

    public String findById(String id) {
        return "Value for ID=" + id;
    }
}
