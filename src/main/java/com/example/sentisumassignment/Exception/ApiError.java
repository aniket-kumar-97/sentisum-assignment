package com.example.sentisumassignment.Exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Builder
@Data
public class ApiError {

  private HttpStatus status;
  private String message;
  private String errors;

  public ApiError(HttpStatus status, String message, String errors) {
    this.status = status;
    this.message = message;
    this.errors = errors;
  }
}
