package com.fiserv.api.ipp.documgmt;

import io.smallrye.jwt.KeyUtils;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;

import javax.json.Json;
import javax.json.JsonObject;
import java.security.PrivateKey;
import java.security.PublicKey;

public class helpers {
    public static void main(String[] args) throws Exception {
        JsonObject userName = Json.createObjectBuilder().add("username", "Alice").build();
        JsonObject userAddress = Json.createObjectBuilder().add("city", "someCity").add("street", "someStreet").build();
        JsonObject json = Json.createObjectBuilder(userName).add("address", userAddress).build();
        JwtClaimsBuilder builder = Jwt.claims(json);

        builder.audience("DocumentsManagementxAPI");
        builder.issuer("Apigee");
        builder.expiresAt(1605657600000l);
        builder.subject("Apigee");
        builder.groups("Admin");

        String jwt = builder.sign(getPrivateKey());

        System.out.println("JWT --->" +jwt);
    }

    private static PrivateKey getPrivateKey() throws Exception {
        return KeyUtils.readPrivateKey("/privateKey.pem");
    }

    private static PublicKey getPublicKey() throws Exception {
        return KeyUtils.readPublicKey("/publicKey.pem");
    }
}
