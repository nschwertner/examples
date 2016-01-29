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


import ca.uhn.fhir.model.api.*;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.valueset.ObservationStatusEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.UriDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;

/**
 * Unit test for simple Application.
 */
@Ignore
public class HeartRateIntegTest extends AbstractIntegTest {

    public static final CodeableConceptDt HEARTRATE_CODE = new CodeableConceptDt("http://loinc.org", "8867-4");

    public static final UriDt HSPC_HEARTRATE_PROFILE_URI =
            new UriDt("http://hl7-fhir.github.io/hspc/observation-hspc-heartrate-hspcheartrate.profile.xml");

    public static final IdDt HSPC_HEARTRATE_PROFILE_ID = new IdDt(HSPC_HEARTRATE_PROFILE_URI);

    @Test
    public void testSimpleValidation() {
        Observation heartRateObservation = new Observation()
                .setCode(HEARTRATE_CODE)
                .setStatus(ObservationStatusEnum.FINAL)
                .setSubject(new ResourceReferenceDt(patient))
                .setValue(new QuantityDt(68))
                .setIssued(new Date(), TemporalPrecisionEnum.SECOND)
                ;
        heartRateObservation.getResourceMetadata().put(ResourceMetadataKeyEnum.PROFILES, HSPC_HEARTRATE_PROFILE_ID);

        FhirValidator validator = session.getFhirContext().newValidator();
        ValidationResult result = validator.validateWithResult(heartRateObservation);
        if (result.isSuccessful()) {
            System.out.println("Validation passed");
        } else {
            // We failed validation!
            System.out.println("Validation failed");
            System.out.println(result.toString());
            Assert.fail();
        }
    }

    @Test
    public void testCreateWithExtentions() {
        Observation heartRateObservation = new Observation()
                .setCode(HEARTRATE_CODE)
                .setStatus(ObservationStatusEnum.FINAL)
                .setSubject(new ResourceReferenceDt(patient))
                .setValue(new QuantityDt(68))
                .setIssued(new Date(), TemporalPrecisionEnum.SECOND);
        heartRateObservation.getResourceMetadata().put(ResourceMetadataKeyEnum.PROFILES, HSPC_HEARTRATE_PROFILE_ID);

        heartRateObservation
                .addUndeclaredExtension(
                        new ExtensionDt(
                                false,
                                "http://hl7.org/fhir/StructureDefinition/observation-bodyPosition",
                                new CodeableConceptDt(
                                        "http://hl7.org/fhir/ValueSet/hspc-heartratebodyposition",
                                        "BODY_POSITION_CODE"
                                )
                        )
                );

        heartRateObservation
                .addUndeclaredExtension(
                        new ExtensionDt(
                                false,
                                "http://hl7.org/fhir/StructureDefinition/observation-hspc-heartratehspc-focalSubject",
                                new CodeableConceptDt(
                                        "http://hl7.org/fhir/ValueSet/hspc-focalSubject",
                                        "FOCAL_SUBJECT_CODE"
                                )
                        )
                );

        FhirValidator validator = session.getFhirContext().newValidator();
        ValidationResult result = validator.validateWithResult(heartRateObservation);
        if (result.isSuccessful()) {
            System.out.println("Validation passed");
        } else {
            // We failed validation!
            System.out.println("Validation failed");
            System.out.println(result.toString());
        }

        MethodOutcome outcome = session.create()
                .resource(heartRateObservation)
                .execute();

        IdDt id = (IdDt) outcome.getId();
        System.out.println("Created Id: " + id);

        // Perform a search for all observations that are of the target profile
        ca.uhn.fhir.model.api.Bundle results = session
                .search()
                .forResource(Observation.class)
//                        Not supported with HAPI 1.1
//                .withTag(BaseHapiFhirDao.NS_JPA_PROFILE, HSPC_HEARTRATE_PROFILE_ID.getValue())
                .execute();
        System.out.println("Found: " + results.size());

        // todo validate upon receiving client side
    }

    @Test
    public void testCreateWithProfileAsTag() {
        Observation heartRateObservation = new Observation()
                .setCode(HEARTRATE_CODE)
                .setSubject(new ResourceReferenceDt(patient))
                .setValue(new QuantityDt(68))
                .setIssued(new Date(), TemporalPrecisionEnum.SECOND);

        // Create a tag list and add it to the resource
        TagList tags = new TagList();
        ResourceMetadataKeyEnum.TAG_LIST.put(heartRateObservation, tags);

        // Add some tags to the list
        tags.addTag(Tag.HL7_ORG_PROFILE_TAG, HSPC_HEARTRATE_PROFILE_ID.getValue());

        MethodOutcome outcome = session.create()
                .resource(heartRateObservation)
                .execute();

        IdDt id = (IdDt) outcome.getId();
        System.out.println("Created Id: " + id);

        // Perform a search for all observations that have been tagged with the profile
        ca.uhn.fhir.model.api.Bundle results = session
                .search()
                .forResource(Observation.class)
//                        Not supported with HAPI 1.1
//                .withTag(Tag.HL7_ORG_PROFILE_TAG, HSPC_HEARTRATE_PROFILE_ID.getValue())
                .execute();
        System.out.println("Found: " + results.size());
    }

    /**
     * Leaves StructureDefinition installed as a side-affect
     */
//    @Test
    public void testSearchDeleteCreate() {
        doSearchDeleteCreate(session, "/src/test/resources/profile/HeartRate.xml", "HSPC Heart Rate");
    }
}
