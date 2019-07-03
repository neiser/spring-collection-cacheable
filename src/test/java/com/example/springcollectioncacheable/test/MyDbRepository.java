package com.example.springcollectioncacheable.test;

import java.util.Map;

public interface MyDbRepository {
    MyValue findById(MyId id);

    Map<MyId, MyValue> findAll();
}
