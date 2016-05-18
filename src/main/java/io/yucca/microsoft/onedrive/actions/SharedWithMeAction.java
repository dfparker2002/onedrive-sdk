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

import java.util.concurrent.Callable;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.ItemCollection;
import io.yucca.microsoft.onedrive.ItemIterable;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;

/**
 * Action to list items that are shared with the user
 * 
 * @author yucca.io
 */
public class SharedWithMeAction extends AbstractAction
    implements Callable<ItemIterable> {

    private static final Logger LOG = LoggerFactory
        .getLogger(SharedWithMeAction.class);

    public static final String ACTION = "view.sharedWithMe";

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     */
    public SharedWithMeAction(OneDriveAPIConnection api) {
        super(api);
    }

    /**
     * List items shared with user
     * 
     * @return ItemIterable
     */
    @Override
    public ItemIterable call() {
        return sharedWithMe();
    }

    private ItemIterable sharedWithMe() {
        LOG.info("Listing items shared with user");
        Response response = api.webTarget().path("/drive" + "/" + ACTION)
            .request(MediaType.APPLICATION_JSON_TYPE).get();
        handleNotModified(response);
        handleError(response, Status.OK, "Failed to items shared with user");
        return response.readEntity(ItemCollection.class)
            .setProvider(new ItemProviderImpl(api));
    }

}
