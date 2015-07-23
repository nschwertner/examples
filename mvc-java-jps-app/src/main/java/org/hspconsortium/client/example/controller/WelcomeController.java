/*
 *
 *  * Copyright (c) 2014. Health Services Platform Consortium. All Rights Reserved.
 *
 */

package org.hspconsortium.client.example.controller;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import org.apache.commons.lang.StringUtils;
import org.hspconsortium.client.example.model.Height;
import org.hspconsortium.client.session.FhirSession;
import org.hspconsortium.client.session.SessionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class WelcomeController {

    @javax.annotation.Resource(name="codeFlowSessionFactory")
    private SessionFactory ehrSessionFactory;

    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public ModelAndView patientSync(ModelAndView modelAndView) {
        FhirSession session = ehrSessionFactory.session();
        String patientId = session.getAccessToken().getPatientId();

        Patient patient = session.read().resource(Patient.class).withId(session.getAccessToken().getPatientId()).execute();

        Bundle results = session.search().forResource(Observation.class).where(
                Observation.SUBJECT.hasId(patientId)).
                and(Observation.CODE.exactly().identifier("8302-2")).execute();


        modelAndView.setViewName("example");
        modelAndView.addObject("patientFullName", StringUtils.join(patient.getName().get(0).getGiven(), " ") + " " +
                patient.getName().get(0).getFamily().get(0));

        List<Height> heights = new ArrayList<Height>();
        for (BundleEntry entry : results.getEntries()) {
            String date = ((DateTimeDt)((Observation)entry.getResource()).getApplies()).getValueAsString();
            String height = ((QuantityDt)((Observation)entry.getResource()).getValue()).getValue().toPlainString();
            heights.add(new Height(height, date));
        }

        modelAndView.addObject("heights", heights);
        return modelAndView;
    }
}
