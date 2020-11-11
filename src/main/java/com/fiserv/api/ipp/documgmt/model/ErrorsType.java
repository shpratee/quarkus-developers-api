package com.fiserv.api.ipp.documgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorsType implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty
    private List<ErrorType> errors = new ArrayList<>();

    public ErrorsType() {
    }

    public ErrorsType(ErrorType error){
        errors = new ArrayList<>();
        errors.add(error);
    }

    public List<ErrorType> add(ErrorType error) {
        errors.add(error);
        return errors;
    }
}
