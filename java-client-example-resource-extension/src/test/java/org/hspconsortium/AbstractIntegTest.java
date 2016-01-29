/*
 * #%L
 * hspc-client-ioc-example
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
package org.hspconsortium;


import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.base.resource.BaseOperationOutcome;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.StructureDefinition;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import org.hspconsortium.client.auth.credentials.ClientSecretCredentials;
import org.hspconsortium.client.example.Application;
import org.hspconsortium.client.session.Session;
import org.hspconsortium.client.session.clientcredentials.ClientCredentialsSessionFactory;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Unit test for simple Application.
 */
public class AbstractIntegTest {

    protected Session session;

    protected Patient patient;

    @Before
    public void setUp() {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(Application.class);
        ClientCredentialsSessionFactory<ClientSecretCredentials> sessionFactory = context.getBean(ClientCredentialsSessionFactory.class);
        session = sessionFactory.createSession();

        // find our patient, Aaron Alexis
        if (patient == null) {
            ca.uhn.fhir.model.api.Bundle results = session
                    .search()
                    .forResource(Patient.class)
                    .where(Patient.FAMILY.matches().value("Alexis"))
                    .execute();
            System.out.println("Patient: " + results.size());
            if (results.size() < 1) {
                throw new RuntimeException("Patient not found");
            }
            for (BundleEntry entry : results.getEntries()) {
                patient = (Patient)entry.getResource();
            }
        }
    }

    /**
     * Leaves StructureDefinition installed as a side-affect
     */
    protected void doSearchDeleteCreate(Session session, String modelPath, String name) {
        // Perform a search
        ca.uhn.fhir.model.api.Bundle results = session
                .search()
                .forResource(StructureDefinition.class)
                .where(StructureDefinition.NAME.matches().value(name))
                .execute();
        System.out.println("Found: " + results.size());

        if (results.size() == 1) {
            for (BundleEntry bundleEntry : results.getEntries()) {
                BaseOperationOutcome resp = session
                        .delete()
                        .resourceById(bundleEntry.getResource().getId())
                        .execute();
                if (resp != null) {
                    System.out.println("Deleted: " + resp.getIssueFirstRep().getDetailsElement().getValue());
                } else {
                    results = session
                            .search()
                            .forResource(StructureDefinition.class)
                            .where(StructureDefinition.NAME.matches().value(name))
                            .execute();
                    System.out.println("After delete: " + results.size());
                }
            }
        }

        // create
        MethodOutcome outcome = session.create()
                .resource(classPathResourceAsXmlString(modelPath))
                .prettyPrint()
                .encodedXml()
                .execute();

        IdDt id = (IdDt) outcome.getId();
        System.out.println("Got ID: " + id.getValue());
    }

    protected static String classPathResourceAsXmlString(String path) {
        try {
            Resource resource = new ClassPathResource(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()), 1024);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            br.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
