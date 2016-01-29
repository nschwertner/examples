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
package org.hspconsortium.client.example;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import org.hspconsortium.client.auth.credentials.Credentials;
import org.hspconsortium.client.session.clientcredentials.ClientCredentialsSessionFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

public interface PatientCompareService {

    int comparePatientName(String patientId);

    @Component
    class Impl implements PatientCompareService {
        @Inject
        ClientCredentialsSessionFactory<? extends Credentials> ehr1SessionFactory;

        @Inject
        ClientCredentialsSessionFactory<? extends Credentials> ehr2SessionFactory;

        public int comparePatientName(String patientId) {
            // look up the patient in ehr1
            System.out.println("Looking up patient in EHR1...");
            Patient patient1 = ehr1SessionFactory
                    .createSession()
                    .read()
                    .resource(Patient.class)
                    .withId(patientId)
                    .execute();
            System.out.println("Found patient: " + patient1.getNameFirstRep().getNameAsSingleString());

            // look up the patient in ehr2
            System.out.println("Looking up patient in EHR2...");
            Patient patient2 = ehr2SessionFactory
                    .createSession()
                    .read()
                    .resource(Patient.class)
                    .withId(patientId)
                    .execute();
            System.out.println("Found patient: " + patient2.getNameFirstRep().getNameAsSingleString());

            // Compare the patient based on their name string
            return patient1.getNameFirstRep().getNameAsSingleString()
                    .compareTo(patient2.getNameFirstRep().getNameAsSingleString());
        }
    }
}
