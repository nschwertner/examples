/*
 * #%L
 * hsp-client-ioc-example
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


import org.hspconsortium.client.example.Application;
import org.hspconsortium.client.example.PatientCompareService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Unit test for simple Application.
 */
@Ignore
public class ComparePatientIntegTest {

    @Test
    public void testApp() {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(Application.class);
        PatientCompareService patientCompareService = context.getBean(PatientCompareService.class);

        String patientId = "ID9995679";

        int result = patientCompareService.comparePatientName(patientId);
        System.out.println("Compare result: " + result);
    }
}
