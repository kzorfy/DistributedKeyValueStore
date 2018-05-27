package com.root.keyvaluestore.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.root.keyvaluestore.model.ErrorMessage;

/*
 * Register with JAX RS of GenericException to be used for internal exceptions to notify clients
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

 @Override
 public Response toResponse(Throwable ex) {
     final ErrorMessage errorMessage = new ErrorMessage(500, ex.getMessage());
     return Response.status(Status.INTERNAL_SERVER_ERROR)
             .entity(errorMessage)
             .build();
 }

}
