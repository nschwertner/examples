/*
 * #%L
 * hsp-client-example
 * %%
 * Copyright (C) 2014 - 2015 Healthcare Services Platform Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.hspconsortium.client.example;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IRestfulClientFactory;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.hspconsortium.client.auth.Scopes;
import org.hspconsortium.client.auth.SimpleScope;
import org.hspconsortium.client.auth.access.AccessTokenProvider;
import org.hspconsortium.client.auth.access.JsonAccessTokenProvider;
import org.hspconsortium.client.auth.credentials.ClientSecretCredentials;
import org.hspconsortium.client.auth.credentials.Credentials;
import org.hspconsortium.client.auth.credentials.JWTCredentials;
import org.hspconsortium.client.controller.FhirEndpointsProvider;
import org.hspconsortium.client.session.clientcredentials.ClientCredentialsSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.UUID;

@Configuration
@PropertySource("classpath:config/config.properties")
public class AppConfig {

    @Autowired
    Environment env;

    @Bean
    public String fhirServicesUrl() {
        return env.getProperty("example.fhirServicesUrl");
    }

    @Bean
    public String clientId() {
        return env.getProperty("example.clientId");
    }

    @Bean
    public String scope() {
        return env.getProperty("example.scopes");
    }

    @Bean
    public String clientSecret() {
        return env.getProperty("example.clientSecret");
    }

    @Bean
    public String jsonWebKeySetLocation() {
        return env.getProperty("example.jsonWebKeySetLocation");
    }

    @Bean
    public Integer httpConnectionTimeOut() {
        return Integer.parseInt(env.getProperty("example.httpConnectionTimeoutMilliSeconds"
                , IRestfulClientFactory.DEFAULT_CONNECT_TIMEOUT + ""));
    }

    @Bean
    public Integer httpReadTimeOut() {
        return Integer.parseInt(env.getProperty("example.httpReadTimeoutMilliSeconds"
                , IRestfulClientFactory.DEFAULT_CONNECTION_REQUEST_TIMEOUT + ""));
    }

    @Bean
    public String proxyPassword() {
        return System.getProperty("http.proxyPassword", System.getProperty("https.proxyPassword"));
    }

    @Bean
    public String proxyUser() {
        return System.getProperty("http.proxyUser", System.getProperty("https.proxyUser"));
    }

    @Bean
    public Integer proxyPort() {
        return Integer.parseInt(System.getProperty("http.proxyPort", System.getProperty("https.proxyPort", "8080")));
    }

    @Bean
    public String proxyHost() {
        //-Dhttp.proxyHost=proxy.host.com -Dhttp.proxyPort=8080  -Dhttp.proxyUser=username -Dhttp.proxyPassword=password
        return System.getProperty("http.proxyHost", System.getProperty("https.proxyHost"));
    }

    @Bean
    public Integer jsonWebKeySetSizeLimitBytes() {
        return Integer.parseInt(env.getProperty("example.jsonWebKeySetSizeLimitBytes", "10000"));
    }

    @Bean
    public Long jsonTokenDuration() {
        return Long.parseLong(env.getProperty("example.tokenDuration", "900"));
    }

    @Bean
    @Inject
    public ClientSecretCredentials clientSecretCredentials(String clientSecret) {
        return new ClientSecretCredentials(clientSecret);
    }

    @Bean
    public AccessTokenProvider tokenProvider(FhirContext fhirContext) {
        return new JsonAccessTokenProvider(fhirContext);
    }

    @Bean
    public FhirEndpointsProvider fhirEndpointsProvider(FhirContext fhirContext) {
        return new FhirEndpointsProvider.Impl(fhirContext);
    }

    @Bean
    @Inject
    public Credentials credentials(String clientSecret, String jsonWebKeySetLocation) {
        if (clientSecret != null) {
            return clientSecretCredentials(clientSecret);
        } else if (jsonWebKeySetLocation != null) {
            return jwtCredentials(
                    jwkSet(jsonWebKeySetLocation(), httpConnectionTimeOut(), httpReadTimeOut(), jsonWebKeySetSizeLimitBytes()),
                    clientId(),
                    null,
                    jsonTokenDuration());
        } else {
            throw new RuntimeException("Credentials not specified");
        }
    }

    @Bean
    public FhirContext fhirContext(Integer httpConnectionTimeOut, Integer httpReadTimeOut
            , String proxyHost, Integer proxyPort
            , String proxyUser, String proxyPassword) {
        FhirContext hapiFhirContext = FhirContext.forDstu2();
        // Set how long to try and establish the initial TCP connection (in ms)
        hapiFhirContext.getRestfulClientFactory().setConnectTimeout(httpConnectionTimeOut);

        // Set how long to block for individual read/write operations (in ms)
        hapiFhirContext.getRestfulClientFactory().setSocketTimeout(httpReadTimeOut);

        if (proxyHost != null) {
            hapiFhirContext.getRestfulClientFactory().setProxy(proxyHost, proxyPort);

            hapiFhirContext.getRestfulClientFactory().setProxyCredentials(proxyUser
                    , proxyPassword);
        }
        return hapiFhirContext;
    }

    @Bean
    @Inject
    // simulate two EHR by having two instances of session factory
    public ClientCredentialsSessionFactory<? extends Credentials> ehr1SessionFactory(
            FhirContext fhirContext, AccessTokenProvider tokenProvider, FhirEndpointsProvider fhirEndpointsProvider, String fhirServicesUrl,
            String clientId, Credentials credentials, String scope) {
        Scopes scopes = new Scopes();
        scopes.add(new SimpleScope(scope));
        return new ClientCredentialsSessionFactory<>(fhirContext, tokenProvider, fhirEndpointsProvider, fhirServicesUrl, clientId,
                credentials, scopes);
    }

    @Bean
    @Inject
    // simulate two EHR by having two instances of session factory
    public ClientCredentialsSessionFactory<? extends Credentials> ehr2SessionFactory(
            FhirContext fhirContext, AccessTokenProvider tokenProvider, FhirEndpointsProvider fhirEndpointsProvider, String fhirServicesUrl,
            String clientId, Credentials credentials, String scope) {
        Scopes scopes = new Scopes();
        scopes.add(new SimpleScope(scope));
        return new ClientCredentialsSessionFactory<>(fhirContext, tokenProvider, fhirEndpointsProvider, fhirServicesUrl, clientId,
                credentials, scopes);
    }

    private JWKSet jwkSet(String jsonWebKeySetLocation, Integer httpConnectionTimeOut
            , Integer httpReadTimeOut
            , Integer jsonWebKeySetSizeLimitBytes) {
        JWKSet jwks = null;
        try {
            if (isUrl(jsonWebKeySetLocation)) {
                URL url = new URL(jsonWebKeySetLocation);
                jwks = JWKSet.load(url, httpConnectionTimeOut, httpReadTimeOut, jsonWebKeySetSizeLimitBytes);
            } else {
                Class currentClass = new Object() {
                }.getClass().getEnclosingClass();
                String fileName = currentClass.getClassLoader().getResource(jsonWebKeySetLocation).getFile();
                jwks = JWKSet.load(new File(fileName));
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        return jwks;
    }

    private JWTCredentials jwtCredentials(JWKSet jwkSet, String clientId, String audience, Long jsonTokenDuration) {
        try {
            RSAKey rsaKey = (RSAKey) jwkSet.getKeys().get(0);
            JWTCredentials credentials = new JWTCredentials(rsaKey.toRSAPrivateKey());
            credentials.setIssuer(clientId);
            credentials.setSubject(clientId);
            credentials.setAudience(audience);
            credentials.setTokenReference(UUID.randomUUID().toString());
            credentials.setDuration(jsonTokenDuration);
            return credentials;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean isUrl(String location) {
        String[] schemes = {"http", "https"};
        org.apache.commons.validator.UrlValidator urlValidator = new org.apache.commons.validator.UrlValidator(schemes);
        return urlValidator.isValid(location);
    }
}
