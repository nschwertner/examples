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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import java.util.UUID;

@Controller
public class StartPageController {

    @Inject
    private String fhirServicesUrl;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView myHeightChart(ModelAndView modelAndView) {
        modelAndView.setViewName("start");
        // Data service URL
        modelAndView.addObject("iss", fhirServicesUrl);
        // some id for this client's communication
        modelAndView.addObject("launch", UUID.randomUUID());

        return modelAndView;
    }
}
