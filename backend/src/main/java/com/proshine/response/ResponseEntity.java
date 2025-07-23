package com.proshine.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseEntity<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ResponseEntity<T> success(T data) {
        return new ResponseEntity<>(200, "success", data);
    }

    public static <T> ResponseEntity<T> fail(String message) {
        return new ResponseEntity<>(500, message, null);
    }
}
