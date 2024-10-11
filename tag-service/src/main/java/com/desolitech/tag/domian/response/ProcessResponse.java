package com.desolitech.tag.domian.response;

public class ProcessResponse<T> {

    public Response<T> success(T value) {
        return new Response<>(value);
    }

    public Response<T> error(String message){
        ErrorResponse errorResponse = new ErrorResponse(message);
        return new Response<>(errorResponse);
    }

    public Response<T> error(ErrorResponse errorResponse){
        return new Response<>(errorResponse);
    }

    public Response<T> error(Exception exception){
        return error(exception.getMessage());
    }

    public Response<T> error(Response response){
        return new Response<>(response.getErrorResponse());
    }
}