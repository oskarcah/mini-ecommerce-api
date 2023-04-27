package com.ecommerce.api.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(BAD_REQUEST)
public class ValidacionExcepcion extends RuntimeException {
  public ValidacionExcepcion(String mensaje, Exception cause) {
    super(mensaje, cause);
  }}
