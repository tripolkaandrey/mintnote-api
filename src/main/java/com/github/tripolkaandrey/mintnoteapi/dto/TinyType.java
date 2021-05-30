package com.github.tripolkaandrey.mintnoteapi.dto;

public class TinyType<T> {
    private T value;

    private TinyType(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public static <T> TinyType<T> of(T value) {
        return new TinyType<>(value);
    }
}