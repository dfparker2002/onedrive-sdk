/**
 * Copyright 2015 Rob Sessink
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.yucca.microsoft.onedrive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilderException;

import org.junit.Test;

import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.QueryParameters;
import io.yucca.microsoft.onedrive.QueryParameters.Builder;
import io.yucca.microsoft.onedrive.resources.Order;
import io.yucca.microsoft.onedrive.resources.Relationship;

/**
 * @author yucca.io
 */
public class QueryParametersTest {

    @Test
    public void testConfigure() throws IllegalArgumentException,
        UriBuilderException, URISyntaxException {
        Client c = ClientBuilder.newClient();
        WebTarget target = c.target(OneDriveAPIConnection.ONEDRIVE_URL)
            .path("/drive/root/children");

        Builder builder = QueryParameters.Builder.newQueryParameters();
        target = builder.expand(Relationship.CHILDREN)
            .select("name", "createdBy").top(10).orderby("name", Order.ASC)
            .skipToken("11").configure(target);
        assertNotNull(target);
        assertEquals(new URI("https://api.onedrive.com/v1.0/drive/root/children?token=11&select=name%2CcreatedBy%2Cid%2Cfile%2Cfolder%2CeTag%2CparentReference&expand=children&orderby=name%20asc&top=10"),
                     target.getUriBuilder().build());
    }

    @Test
    public void testToString() {
        Builder builder = QueryParameters.Builder.newQueryParameters();
        QueryParameters parameters = builder.expand(Relationship.CHILDREN)
            .select("name", "createdBy").top(10).orderby("name", Order.ASC)
            .skipToken("11").build();
        assertNotNull(parameters);
        assertEquals("?token=11&select=name,createdBy,id,file,folder,eTag,parentReference&expand=children&orderby=name asc&top=10",
                     parameters.toString());
    }

}
