package com.cobanking.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseApiResponse<T> {
    private int status; // 1 = success, 0 = failure
    private String message;
    private T data;

    public BaseApiResponse() {
    }

    public BaseApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> BaseApiResponse<T> success(String message, T data) {
        return new BaseApiResponse<>(1, message, data);
    }

    public static <T> BaseApiResponse<T> failure(String message, T data) {
        return new BaseApiResponse<>(0, message, data);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
