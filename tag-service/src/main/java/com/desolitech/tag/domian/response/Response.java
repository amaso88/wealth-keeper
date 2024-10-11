package com.desolitech.tag.domian.response;

public class Response<T> {
    private final Boolean success;
    private T value;
    private ErrorResponse errorResponse;

    public Response(T value) {
        this.success = true;
        this.value = value;
    }

    public Response(ErrorResponse errorResponse) {
        this.success = false;
        this.errorResponse = errorResponse;
    }

    public Boolean isSuccess() {
        return success;
    }

    public Boolean hasError() {
        return !success;
    }

    public Boolean isSuccessAndHasValue() {
        return success && value != null;
    }

    public T getValue() {
        return value;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

}
