package com.example.springcollectioncacheable;

import org.springframework.stereotype.Component;

@Component
public class MyDbRepository {

    public MyValue findById(MyId id) {
        return new MyValue("Value for ID=" + id);
    }
}
