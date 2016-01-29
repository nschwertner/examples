/*
 * #%L
 * hspc-client-example
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
import org.hspconsortium.client.auth.StateProvider;
import org.hspconsortium.client.auth.access.AccessTokenProvider;
import org.hspconsortium.client.auth.access.JsonAccessTokenProvider;
import org.hspconsortium.client.auth.authorizationcode.AuthorizationCodeRequestBuilder;
import org.hspconsortium.client.auth.credentials.ClientSecretCredentials;
import org.hspconsortium.client.controller.FhirEndpointsProvider;
import org.hspconsortium.client.session.FhirSessionContextHolder;
import org.hspconsortium.client.session.SessionKeyRegistry;
import org.hspconsortium.client.session.authorizationcode.AuthorizationCodeSessionFactory;
import org.hspconsortium.client.session.impl.SimpleFhirSessionContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.inject.Inject;

@Configuration
@PropertySource("classpath:config/config.properties")
@EnableWebMvc
public class AppConfig {

    @Autowired
    Environment env;

    @Bean
    public String clientId() {
        return env.getProperty("example.clientId");
    }

    @Bean
    public String scope() {
        return env.getProperty("example.scopes");
    }

    @Bean
    public String redirectUri() {
        return env.getProperty("example.redirectUri");
    }

    @Bean
    public String clientSecret() {
        return env.getProperty("example.clientSecret");
    }

    @Bean
    public String appEntryPoint() {
        return env.getProperty("example.appEntryPoint");
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
        //To Use With Proxy
        //-Dhttp.proxyHost=proxy.host.com -Dhttp.proxyPort=8080  -Dhttp.proxyUser=username -Dhttp.proxyPassword=password
        return System.getProperty("http.proxyHost", System.getProperty("https.proxyHost"));
    }

    @Bean
    public FhirEndpointsProvider fhirEndpointsProvider(FhirContext fhirContext) {
        return new FhirEndpointsProvider.Impl(fhirContext);
    }

    @Bean
    public StateProvider stateProvider() {
        return new StateProvider.DefaultStateProvider();
    }

    @Bean
    public FhirSessionContextHolder fhirSessionContextHolder() {
        return new SimpleFhirSessionContextHolder();
    }

    @Bean
    public AccessTokenProvider accessTokenProvider(FhirContext fhirContext) {
        return new JsonAccessTokenProvider(fhirContext);
    }

    @Bean
    @Inject
    public AuthorizationCodeRequestBuilder authorizationCodeRequestBuilder(FhirEndpointsProvider fhirEndpointsProvider,
                                                                           StateProvider stateProvider) {
        return new AuthorizationCodeRequestBuilder(fhirEndpointsProvider, stateProvider);
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
    public ClientSecretCredentials clientSecretCredentials(String clientSecret) {
        return new ClientSecretCredentials(clientSecret);
    }

    @Bean
    public SessionKeyRegistry sessionKeyRegistry() {
        return new SessionKeyRegistry();
    }

    @Bean
    @Inject
    public AuthorizationCodeSessionFactory<ClientSecretCredentials>
    authorizationCodeSessionFactory(FhirContext fhirContext, SessionKeyRegistry sessionKeyRegistry,
                                    FhirSessionContextHolder fhirSessionContextHolder,
                                    AccessTokenProvider patientAccessTokenProvider,
                                    String clientId, ClientSecretCredentials clientSecretCredentials, String redirectUri) {
        return new AuthorizationCodeSessionFactory<>(fhirContext, sessionKeyRegistry, "MySessionKey", fhirSessionContextHolder,
                patientAccessTokenProvider, clientId, clientSecretCredentials, redirectUri);
    }
}
