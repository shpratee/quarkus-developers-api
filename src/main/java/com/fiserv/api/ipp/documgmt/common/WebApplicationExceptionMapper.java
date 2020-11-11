package com.fiserv.api.ipp.documgmt.common;

import com.fiserv.api.ipp.documgmt.model.ErrorType;
import com.fiserv.api.ipp.documgmt.model.ErrorsType;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException e) {
        System.out.println("In WebApplicationExceptionMapper");

        ErrorType error = ErrorType.builder()
                .setCode("INTERNAL_SERVER_ERROR")
                .setTitle("The request failed due to an internal error.")
                .build();

        return Response.serverError().entity(new ErrorsType(error)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
