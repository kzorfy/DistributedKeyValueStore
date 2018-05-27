package com.root.keyvaluestore.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.root.keyvaluestore.model.ErrorMessage;

/*
 * Register with JAX RS of DataNotFoundException to be used for 404 status code to notify clients
 */
@Provider
public class DataNotFoundExceptionMapper implements ExceptionMapper<DataNotFoundException> {

    @Override
    public Response toResponse(final DataNotFoundException ex) {
        final ErrorMessage errorMessage = new ErrorMessage(404, ex.getMessage());
        return Response.status(Status.NOT_FOUND)
                .entity(errorMessage)
                .build();
    }

}
