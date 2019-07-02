package com.example.springcollectioncacheable;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class MyDbRepository {

    public MyValue findById(MyId id) {
        return new MyValue("Value for ID=" + id);
    }

    public Map<MyId, MyValue> findAll() {
        return Collections.emptyMap();
    }
}
