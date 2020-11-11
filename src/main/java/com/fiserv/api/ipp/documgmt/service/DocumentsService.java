package com.fiserv.api.ipp.documgmt.service;

import com.fiserv.api.ipp.documgmt.DocumentsResource;
import com.fiserv.api.ipp.documgmt.helper.ClientProducer;
import com.fiserv.api.ipp.documgmt.helper.DocumentsConstants;
import com.fiserv.api.ipp.documgmt.mapper.DocumentsMapper;
import com.fiserv.api.ipp.documgmt.model.*;
import com.fiserv.api.ipp.documgmt.model.Document;
import com.fiserv.api.ipp.documgmt.model.client.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@ApplicationScoped
public class DocumentsService {

    @ConfigProperty(name="documents.iapi.root")
    String documentsIapiRoot;

    @Inject
    DocumentsMapper mapper;

    @Inject
    ClientProducer clientProducer;

    public Response uploadDocument(CreateDocumentType document, HttpHeaders httpHeaders) {
        String allianceCode = httpHeaders.getRequestHeader(DocumentsConstants.ALLIANCE_CODE_HEADER).get(0);
        String interactionId = httpHeaders.getRequestHeader(DocumentsConstants.FISV_INTERACTION_ID_HEADER).get(0);
        String partnerCode = null;
        if(httpHeaders.getRequestHeader(DocumentsConstants.PARTNER_CODE_HEADER) != null && httpHeaders.getRequestHeader(DocumentsConstants.PARTNER_CODE_HEADER).size() > 0){
            partnerCode = httpHeaders.getRequestHeader(DocumentsConstants.PARTNER_CODE_HEADER).get(0);
        }

        com.fiserv.api.ipp.documgmt.model.client.Document createDocumentRequest = new com.fiserv.api.ipp.documgmt.model.client.Document();
        createDocumentRequest.setTitle(document.getTitle());
        createDocumentRequest.setType(document.getType().name());
        createDocumentRequest.setDescription(document.getDescription());

        DocumentContent docContent = new DocumentContent();
        docContent.setFileName(document.getData().getName());
        docContent.setFileContent(document.getData().getContent());

        createDocumentRequest.setFileContent(docContent);

        Map<String, String> metaMap = document.getMeta();
        List<DomainField> domainFields  = new ArrayList<>();
        if(metaMap != null && metaMap.size() > 0){
            mapper.convertMetaToDomainFields(domainFields, metaMap);
        }

        domainFields.add(new DomainField(DocumentsConstants.ALLIANCE_CODE, allianceCode));

        if(partnerCode != null){
            domainFields.add(new DomainField(DocumentsConstants.PARTNER_CODE, partnerCode));
        }

        createDocumentRequest.setDomainFields(domainFields.toArray(new DomainField[domainFields.size()]));

        Entity<com.fiserv.api.ipp.documgmt.model.client.Document> request = Entity.entity(createDocumentRequest,MediaType.APPLICATION_JSON);

        WebTarget webTarget = clientProducer.produceClient()
                .target(documentsIapiRoot)
                .path("/documents")
                .queryParam(DocumentsConstants.ALLIANCE_CODE_PARAM,allianceCode)
                .queryParam(DocumentsConstants.TYPE_PARAM,document.getType())
                .queryParam(DocumentsConstants.SOURCE_PLATFORM_PARAM, DocumentsConstants.XAPI_SOURCE_PLATFORM);

        if(partnerCode != null){
            webTarget = webTarget.queryParam(DocumentsConstants.PARTNER_CODE_PARAM, partnerCode);
        }

        Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header(DocumentsConstants.CORRELATION_ID_HEADER, interactionId)
                .header(DocumentsConstants.CONTENT_TYPE_HEADER, DocumentsConstants.APPLICATION_JSON_CONTENT_TYPE)
                .post(request);

        if(response.getStatus() == 201){
            DocumentItemResponse documentItemResponse = response.readEntity(DocumentItemResponse.class);
            DocumentItemResponseType docItemResponseType = mapper.convertDocumentItemResponseToDocumentItemResponseType(documentItemResponse, null);

            return Response.created(UriBuilder.fromResource(DocumentsResource.class).
                    path(docItemResponseType.getData().getId()).build()).entity(docItemResponseType).build();
         } else {
            ErrorsType errors = response.readEntity(ErrorsType.class);
            DocumentCollectionResponseType responseType = new DocumentCollectionResponseType();
            responseType.setErrors(errors);

            return Response.status(response.getStatus()).entity(errors).build();
        }
    }

    public Response retrieveDocument(String category, String documentId, String documentType, HttpHeaders httpHeaders) {
        String allianceCode = httpHeaders.getRequestHeader(DocumentsConstants.ALLIANCE_CODE_HEADER).get(0);
        String interactionId = httpHeaders.getRequestHeader(DocumentsConstants.FISV_INTERACTION_ID_HEADER).get(0);
        String partnerCode = null;
        if(httpHeaders.getRequestHeader(DocumentsConstants.PARTNER_CODE_HEADER) != null && httpHeaders.getRequestHeader(DocumentsConstants.PARTNER_CODE_HEADER).size() > 0){
            partnerCode = httpHeaders.getRequestHeader(DocumentsConstants.PARTNER_CODE_HEADER).get(0);
        }

        WebTarget webTarget = clientProducer.produceClient()
                .target(documentsIapiRoot)
                .path("/documents/{documentId}")
                .resolveTemplate("documentId", documentId)
                .queryParam(DocumentsConstants.ALLIANCE_CODE_PARAM,allianceCode)
                .queryParam(DocumentsConstants.SOURCE_PLATFORM_PARAM, DocumentsConstants.XAPI_SOURCE_PLATFORM)
                .queryParam(DocumentsConstants.TYPE_PARAM, documentType);

        if(partnerCode != null){
            webTarget = webTarget.queryParam(DocumentsConstants.PARTNER_CODE_PARAM, partnerCode);
        }
        if(category != null){
            webTarget = webTarget.queryParam(DocumentsConstants.CATEGORY_PARAM, category);
        }

        Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header(DocumentsConstants.CORRELATION_ID_HEADER, interactionId)
                .get();

        if(response.getStatus() == 200){
            DocumentItemResponse documentItemResponse = response.readEntity(DocumentItemResponse.class);
            return Response.ok().entity(mapper.convertDocumentItemResponseToDocumentItemResponseType(documentItemResponse, category)).build();
        } else {
            ErrorsType errors = response.readEntity(ErrorsType.class);
            DocumentCollectionResponseType responseType = new DocumentCollectionResponseType();
            responseType.setErrors(errors);

            return Response.status(response.getStatus()).entity(errors).build();
        }
    }

    public Response updateDocument(String documentId, Document document, HttpHeaders httpHeaders) {
        String allianceCode = httpHeaders.getRequestHeader(DocumentsConstants.ALLIANCE_CODE_HEADER).get(0);
        String interactionId = httpHeaders.getRequestHeader(DocumentsConstants.FISV_INTERACTION_ID_HEADER).get(0);
        String partnerCode = null;
        if(httpHeaders.getRequestHeader(DocumentsConstants.PARTNER_CODE_HEADER) != null && httpHeaders.getRequestHeader(DocumentsConstants.PARTNER_CODE_HEADER).size() > 0){
            partnerCode = httpHeaders.getRequestHeader(DocumentsConstants.PARTNER_CODE_HEADER).get(0);
        }

        com.fiserv.api.ipp.documgmt.model.client.Document updateDocumentRequest = new com.fiserv.api.ipp.documgmt.model.client.Document();

        updateDocumentRequest.setId(documentId);
        updateDocumentRequest.setType(document.getType());
        updateDocumentRequest.setTitle(document.getTitle());
        updateDocumentRequest.setDescription(document.getDescription());

        if(document.getData() != null){
            DocumentContent fileContent = new DocumentContent();
            fileContent.setFileName(document.getData().getName());
            fileContent.setFileContent(document.getData().getContent());
            updateDocumentRequest.setFileContent(fileContent);
        }

        Map<String, String> metaMap = document.getMeta();
        List<DomainField> domainFields  = new ArrayList<>();
        if(metaMap != null && metaMap.size() > 0){
            mapper.convertMetaToDomainFields(domainFields, metaMap);
        }

        updateDocumentRequest.setDomainFields(domainFields.toArray(new DomainField[domainFields.size()]));

        Entity<com.fiserv.api.ipp.documgmt.model.client.Document> request = Entity.entity(updateDocumentRequest,MediaType.APPLICATION_JSON);

        WebTarget webTarget  = clientProducer.produceClient()
                .target(documentsIapiRoot)
                .path("/documents/{documentId}")
                .resolveTemplate("documentId", documentId)
                .queryParam(DocumentsConstants.ALLIANCE_CODE_PARAM,allianceCode)
                .queryParam(DocumentsConstants.SOURCE_PLATFORM_PARAM, DocumentsConstants.XAPI_SOURCE_PLATFORM)
                .queryParam(DocumentsConstants.TYPE_PARAM, document.getType());

        if(partnerCode != null){
            webTarget = webTarget.queryParam(DocumentsConstants.PARTNER_CODE_PARAM, partnerCode);
        }

        Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header(DocumentsConstants.CORRELATION_ID_HEADER, interactionId)
                .header(DocumentsConstants.CONTENT_TYPE_HEADER, DocumentsConstants.APPLICATION_JSON_CONTENT_TYPE)
                .method("PATCH", request);

        if(response.getStatus() == 200){
            DocumentItemResponse documentItemResponse = response.readEntity(DocumentItemResponse.class);
            return Response.ok().entity(mapper.convertDocumentItemResponseToDocumentItemResponseType(documentItemResponse, null)).build();
        } else {
            ErrorsType errors = response.readEntity(ErrorsType.class);
            DocumentCollectionResponseType responseType = new DocumentCollectionResponseType();
            responseType.setErrors(errors);

            return Response.status(response.getStatus()).entity(errors).build();
        }
    }

    public Response retrieveDocuments(String sort, String offset, String limit, String category, String type, String createdBefore, String createdAfter, String modifiedBefore, String modifiedAfter, HttpHeaders httpHeaders) {
        String allianceCode = httpHeaders.getRequestHeader(DocumentsConstants.ALLIANCE_CODE_HEADER).get(0);
        String interactionId = httpHeaders.getRequestHeader(DocumentsConstants.FISV_INTERACTION_ID_HEADER).get(0);

        String partnerCode = null;
        if(httpHeaders.getRequestHeader(DocumentsConstants.PARTNER_CODE_HEADER) != null && httpHeaders.getRequestHeader(DocumentsConstants.PARTNER_CODE_HEADER).size() > 0){
            partnerCode = httpHeaders.getRequestHeader(DocumentsConstants.PARTNER_CODE_HEADER).get(0);
        }

        WebTarget webTarget = clientProducer.produceClient()
                .target(documentsIapiRoot)
                .path("/documents/search")
                .queryParam(DocumentsConstants.ALLIANCE_CODE_PARAM,allianceCode)
                .queryParam(DocumentsConstants.SOURCE_PLATFORM_PARAM, DocumentsConstants.XAPI_SOURCE_PLATFORM);

        if(sort != null) {
            webTarget=webTarget.queryParam(DocumentsConstants.SORT_PARAM, sort);
        }
        if(offset != null){
            webTarget= webTarget.queryParam(DocumentsConstants.OFFSET_PAGINATION_PARAM, offset);
        }
        if(limit != null){
            webTarget= webTarget.queryParam(DocumentsConstants.LIMIT_PAGINATION_PARAM, limit);
        }
        if(category != null){
            webTarget= webTarget.queryParam(DocumentsConstants.CATEGORY_PARAM, category);
        }
        if(partnerCode != null){
            webTarget = webTarget.queryParam(DocumentsConstants.PARTNER_CODE_PARAM, partnerCode);
        }

        SearchDocumentRequest searchDocumentRequest = new SearchDocumentRequest();

        if(type != null) {
            searchDocumentRequest.setDocumentType(type);
        }
        if(createdAfter != null){
            searchDocumentRequest.setCreatedAfter(OffsetDateTime.parse(createdAfter, DateTimeFormatter.ofPattern(DocumentsConstants.DATE_FORMAT)));
        }
        if(createdBefore != null){
            searchDocumentRequest.setCreatedBefore(OffsetDateTime.parse(createdBefore, DateTimeFormatter.ofPattern(DocumentsConstants.DATE_FORMAT)));
        }
        if(modifiedAfter != null){
            searchDocumentRequest.setModifiedAfter(OffsetDateTime.parse(modifiedAfter, DateTimeFormatter.ofPattern(DocumentsConstants.DATE_FORMAT)));
        }
        if(modifiedBefore != null){
            searchDocumentRequest.setModifiedBefore(OffsetDateTime.parse(modifiedBefore, DateTimeFormatter.ofPattern(DocumentsConstants.DATE_FORMAT)));
        }

        List<DomainField> domainFields  = new ArrayList<>();
        domainFields.add(new DomainField(DocumentsConstants.ALLIANCE_CODE, allianceCode));
        if(partnerCode != null){
            domainFields.add(new DomainField(DocumentsConstants.PARTNER_CODE, partnerCode));
        }

        searchDocumentRequest.setDomainFields(domainFields.toArray(new DomainField[domainFields.size()]));

        Entity<SearchDocumentRequest> request = Entity.entity(searchDocumentRequest,MediaType.APPLICATION_JSON);

        Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header(DocumentsConstants.CORRELATION_ID_HEADER, interactionId)
                .post(request);

        if(response.getStatus() == 200){
            DocumentCollectionResponse documentCollectionResponse = response.readEntity(DocumentCollectionResponse.class);
            return Response.ok().entity(mapper.convertDocumentCollectionResponseToDocumentCollectionResponseType(documentCollectionResponse, category)).build();
        } else {
            ErrorsType errors = response.readEntity(ErrorsType.class);
            DocumentCollectionResponseType responseType = new DocumentCollectionResponseType();
            responseType.setErrors(errors);

            return Response.status(response.getStatus()).entity(errors).build();
        }
    }
}