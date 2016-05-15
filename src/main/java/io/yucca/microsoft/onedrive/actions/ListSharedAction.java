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
package io.yucca.microsoft.onedrive.actions;

import java.util.concurrent.Callable;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.ItemCollection;
import io.yucca.microsoft.onedrive.ItemIterable;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.addressing.SharedAddress;

/**
 * Action to list shared items by a user in a drive
 * 
 * @author yucca.io
 */
public class ListSharedAction extends AbstractAction
    implements Callable<ItemIterable> {

    private static final Logger LOG = LoggerFactory
        .getLogger(ListSharedAction.class);

    private final SharedAddress address;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     */
    public ListSharedAction(OneDriveAPIConnection api) {
        super(api);
        this.address = new SharedAddress();
    }

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param address SharedAddress
     */
    public ListSharedAction(OneDriveAPIConnection api, SharedAddress address) {
        super(api);
        this.address = address;
    }

    /**
     * List shared item in drive
     * 
     * @return ItemIterable
     */
    @Override
    public ItemIterable call() {
        return listShared();
    }

    private ItemIterable listShared() {
        LOG.info("Listing shared items in drive: {}", address);
        WebTarget target = api.webTarget().path(address.getPath());
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
            .get();
        handleNotModified(response);
        handleError(response, Status.OK,
                    "Failed to list shared item:" + address);
        return response.readEntity(ItemCollection.class).setApi(api);
    }

}