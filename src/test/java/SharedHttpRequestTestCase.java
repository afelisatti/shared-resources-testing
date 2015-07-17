/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.tck.junit4.DomainFunctionalTestCase;

import org.junit.Test;

public class SharedHttpRequestTestCase extends DomainFunctionalTestCase
{

    private static final String OK_REQUEST_APP_NAME = "okRequestApp";
    private static final String NO_CONTENT_REQUEST_APP_NAME = "noContentRequestApp";

    @Override
    protected String getDomainConfig()
    {
        return "mule-domain-config.xml";
    }

    @Override
    public ApplicationConfig[] getConfigResources()
    {
        return new ApplicationConfig[]{
                new ApplicationConfig(OK_REQUEST_APP_NAME, new String[] {"mule-app-200.xml"}),
                new ApplicationConfig(NO_CONTENT_REQUEST_APP_NAME, new String[] {"mule-app-204.xml"})
        };
    }

    @Test
    public void testSharedResource() throws Exception
    {
        //test ok request app
        MuleMessage responseFromFirstApp = getResponseFromApp("http://localhost:8081/getResponse", OK_REQUEST_APP_NAME);
        assertThat(responseFromFirstApp.getPayloadAsString(), is("200"));

        //test bad request app
        MuleMessage responseFromSecondApp = getResponseFromApp("vm://doRequest", NO_CONTENT_REQUEST_APP_NAME);
        assertThat(responseFromSecondApp.getPayloadAsString(), is("204"));
    }

    private MuleMessage getResponseFromApp(String url, String appName) throws MuleException
    {
        MuleContext muleContext = getMuleContextForApp(appName);
        MuleMessage testMessage = new DefaultMuleMessage(null, muleContext);

        return muleContext.getClient().send(url, testMessage);
    }

}
