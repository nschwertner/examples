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

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import org.apache.commons.lang.StringUtils;
import org.hspconsortium.client.auth.credentials.ClientSecretCredentials;
import org.hspconsortium.client.example.model.Height;
import org.hspconsortium.client.session.Session;
import org.hspconsortium.client.session.authorizationcode.AuthorizationCodeSessionFactory;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PatientHeightChartController {

    @Inject
    private AuthorizationCodeSessionFactory<ClientSecretCredentials> ehrSessionFactory;

    @RequestMapping(value = "/patientHeightChart", method = RequestMethod.GET)
    public ModelAndView patientHeightChart(ModelAndView modelAndView, HttpSession httpSession, HttpRequest httpRequest) {
        // retrieve the EHR session from the http session
        Session ehrSession = (Session) httpSession.getAttribute(ehrSessionFactory.getSessionKey());

        String patientId = ehrSession.getAccessToken().getPatientId();

        Patient patient = ehrSession.read().resource(Patient.class).withId(patientId).execute();

        Bundle results = ehrSession
                .search()
                .forResource(Observation.class)
                .where(
                        Observation.SUBJECT.hasId(patientId)).
                        and(Observation.CODE.exactly().identifier("8302-2"))
                .execute();

        modelAndView.setViewName("example");
        modelAndView.addObject("patientFullName", StringUtils.join(patient.getName().get(0).getGiven(), " ") + " " +
                patient.getName().get(0).getFamily().get(0));

        List<Height> heights = new ArrayList<>();
        for (BundleEntry entry : results.getEntries()) {
            String date = ((DateTimeDt) ((Observation) entry.getResource()).getEffective()).getValueAsString();
            String height = ((QuantityDt) ((Observation) entry.getResource()).getValue()).getValue().toPlainString();
            heights.add(new Height(height, date));
        }

        modelAndView.addObject("heights", heights);
        return modelAndView;
    }
}
