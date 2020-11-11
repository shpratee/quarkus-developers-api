package com.fiserv.api.ipp.documgmt.common;

import com.fiserv.api.ipp.documgmt.model.ErrorType;
import com.fiserv.api.ipp.documgmt.model.ErrorsType;

import javax.annotation.Priority;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.UUID;

import static javax.ws.rs.Priorities.USER;

@Provider
public class CommonExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable e) {
        System.out.println("In CommonExceptionMapper");

        e.printStackTrace();

        ErrorType error = ErrorType.builder()
                .setCode("INTERNAL_SERVER_ERROR")
                .setTitle("The request failed due to an internal error.")
                .build();

        return Response.serverError().entity(new ErrorsType(error)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
