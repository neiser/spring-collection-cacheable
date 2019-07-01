package com.example.springcollectioncacheable;

import java.util.Objects;
import java.util.StringJoiner;

public class MyValue {
    private final String value;

    public MyValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MyValue myValue = (MyValue) o;
        return Objects.equals(value, myValue.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MyValue.class.getSimpleName() + "[", "]")
                .add("value='" + value + "'")
                .toString();
    }
}
