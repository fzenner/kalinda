package com.kewebsi.errorhandling;

public record DataOrError<T>(T data, ErrorUpdate error) {

    public DataOrError(T data) {
        this(data, null);
    }

    public DataOrError(ErrorUpdate error) {
        this(null, error);
    }

    public boolean hasError() {
        return error != null;
    }

    public boolean hasData() {
        return data != null;
    }

    public T getData() {
        return data;
    }

    public ErrorUpdate getError() {
        return error;
    }

    public static <T> DataOrError<T> of(T data) {
        return new DataOrError<>(data);
    }

    public static <T> DataOrError<T> ofError(ErrorUpdate error) {
        return new DataOrError<>(error);
    }
}
