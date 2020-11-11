package com.fiserv.api.ipp.documgmt.common;

import com.fiserv.api.ipp.documgmt.model.ErrorType;
import com.fiserv.api.ipp.documgmt.model.ErrorTypeBuilder;
import com.fiserv.api.ipp.documgmt.model.ErrorsType;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Provider
public class BeanValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(createErrorMessages(e))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    private ErrorsType createErrorMessages(ConstraintViolationException cve) {
        ErrorsType errors  = new ErrorsType();

        for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
            ErrorTypeBuilder builder = ErrorType.builder();

            String defaultMessage = violation.getMessage();
            if (defaultMessage.contains(":")) {
                String[] parts = defaultMessage.split(":");
                if (parts.length == 2) {
                    builder.setCode(parts[0]).setTitle(parts[1]);
                } else {
                    builder.setCode("VALIDATION_ERROR").setTitle(parts[0]);
                }
            } else {
                builder.setCode("VALIDATION_ERROR").setTitle(defaultMessage);
            }

            if (violation.getPropertyPath() != null) {
                Iterator<Path.Node> iterator = violation.getPropertyPath().iterator();
                while (iterator.hasNext()) {
                    Path.Node node = iterator.next();

                    if (node.getKind() == ElementKind.PARAMETER) {
                        builder.setSourceParameter(node.getName());
                        break;
                    }
                }

                if(builder.getSourceParameter() == null){
                    builder.setSourcePointer(violation.getPropertyPath().toString());
                }
            }

            errors.add(builder.build());
        }

        return errors;
    }
}
