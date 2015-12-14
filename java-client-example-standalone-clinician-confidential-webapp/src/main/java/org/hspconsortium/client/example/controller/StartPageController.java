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

package org.hspconsortium.client.example.controller;

import org.hspconsortium.client.auth.authorizationcode.AuthorizationCodeRequest;
import org.hspconsortium.client.auth.authorizationcode.AuthorizationCodeRequestBuilder;
import org.hspconsortium.client.session.authorizationcode.AuthorizationCodeSessionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class StartPageController {

    @Inject
    private String fhirServicesUrl;

    @Inject
    private AuthorizationCodeRequestBuilder authorizationCodeRequestBuilder;

    @Inject
    private AuthorizationCodeSessionFactory authorizationCodeSessionFactory;

    @Inject
    private String clientId;

    @Inject
    private String redirectUri;

    @Inject
    private String scope;


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public void handleStandAloneLaunchRequest(HttpServletRequest request, HttpServletResponse response) {

        AuthorizationCodeRequest authorizationCodeRequest = authorizationCodeRequestBuilder
                .buildStandAloneAuthorizationCodeRequest(fhirServicesUrl, clientId, scope, redirectUri);

        // remember the fhirSessionContext based on the state (for request-callback association)
        authorizationCodeSessionFactory.registerInContext(authorizationCodeRequest.getOauthState(), authorizationCodeRequest);

        // build the response redirect
        response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
        response.setHeader("Location", authorizationCodeRequest.getFhirEndpoints().getAuthorizationEndpoint() +
                "?client_id=" + authorizationCodeRequest.getClientId() +
                "&response_type=" + authorizationCodeRequest.getResponseType() +
                "&scope=" + authorizationCodeRequest.getScopes().asParamValue() +
                "&redirect_uri=" + authorizationCodeRequest.getRedirectUri() +
                "&aud=" + authorizationCodeRequest.getFhirEndpoints().getFhirServiceApi() +
                "&state=" + authorizationCodeRequest.getOauthState()
        );
    }
}
