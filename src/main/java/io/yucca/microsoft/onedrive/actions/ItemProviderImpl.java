/**
 * Copyright 2016 Rob Sessink
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
package io.yucca.microsoft.onedrive.actions;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.yucca.microsoft.onedrive.ItemCollection;
import io.yucca.microsoft.onedrive.ItemIterable;
import io.yucca.microsoft.onedrive.ItemProvider;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;

/**
 * ItemProviderImpl
 * 
 * @author yucca.io
 */
public class ItemProviderImpl implements ItemProvider {

    private final OneDriveAPIConnection api;

    public ItemProviderImpl(OneDriveAPIConnection api) {
        this.api = api;
    }

    /**
     * List children by URL, used to fetch the next item collection.
     * 
     * @param uri URI to an item collection
     * @return ItemIterable
     */
    public ItemIterable byURI(URI uri) {
        Response response = api.webTarget(uri)
            .request(MediaType.APPLICATION_JSON_TYPE).get();
        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new OneDriveException("Failure listing children by URL: "
                                        + uri, response.getStatus());
        }
        return response.readEntity(ItemCollection.class).setProvider(this);
    }
}
