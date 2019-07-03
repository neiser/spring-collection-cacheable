package com.example.springcollectioncacheable.test;

import java.util.Objects;
import java.util.StringJoiner;

public class MyId {
    private final String id;

    public MyId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MyId myKey = (MyId) o;
        return Objects.equals(id, myKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MyId.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .toString();
    }
}
