/*
 * #%L
 * Health Services Platform Consortium - HSP Examples - Spring IOC
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
package org.hspconsortium.utils;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.ProxySelector;
import java.util.Properties;

public class ProxyConfigurationTest {
    private static final String JSON_WEB_KEY_SET_URL = "https://hspc.isalusconsulting.com/dstu2/hsp-reference-authorization/jwk";
    private static final int TIMEOUT = 600;

    @Before
    public void prepareProxysettings() {
        // try to get system preferences for proxy-settings
        Properties props = System.getProperties();
        props.setProperty("java.net.useSystemProxies", "true");
    }

    @Test
    public void testHttpProxyGetJSONWebKeySetFromHSPCSandbox() throws ClientProtocolException, IOException {
        RequestConfig config = RequestConfig.custom().setSocketTimeout(TIMEOUT * 1000)
                .setConnectTimeout(TIMEOUT * 1000).setConnectionRequestTimeout(TIMEOUT * 1000).build();

        BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(System.getProperty("http.proxyHost", System.getProperty("https.proxyHost")),
                        Integer.parseInt(System.getProperty("http.proxyPort", System.getProperty("https.proxyPort", "8080")))),
                new UsernamePasswordCredentials(System.getProperty("http.proxyUser", System.getProperty("https.proxyUser", "user_name"))
                        , System.getProperty("http.proxyPassword", System.getProperty("https.proxyPassword", "password"))));

        CloseableHttpClient httpclient = HttpClientBuilder.create()
                .setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
                .setDefaultCredentialsProvider(credsProvider)
                .setDefaultRequestConfig(config).build();


        try {
            HttpGet httpget = new HttpGet(JSON_WEB_KEY_SET_URL);
            System.out.println("Executing request " + httpget.getRequestLine());

            ResponseHandler<Boolean> resStreamHandler = new ResponseHandler<Boolean>() {

                @Override
                public Boolean handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        String responseString = EntityUtils.toString(entity, "UTF-8");
                        System.out.println(responseString);
                        return true;
                    } else {
                        String responseString = new BasicResponseHandler().handleResponse(response);
                        System.out.println(responseString);
//                        String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
//                        System.out.println(responseString);
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };

            httpclient.execute(httpget, resStreamHandler);
        } catch (Exception ex) {

        } finally {
            httpclient.close();
        }
    }
}
