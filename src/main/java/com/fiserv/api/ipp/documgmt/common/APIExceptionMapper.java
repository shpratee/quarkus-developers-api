package com.fiserv.api.ipp.documgmt.common;

import com.fiserv.api.ipp.documgmt.exception.APIException;
import com.fiserv.api.ipp.documgmt.model.ErrorsType;

import javax.annotation.Priority;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class APIExceptionMapper implements ExceptionMapper<APIException> {

    @Override
    public Response toResponse(APIException e) {
       return Response.status(e.getHttpStatus()).entity(new ErrorsType(e.getErrorType())).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
